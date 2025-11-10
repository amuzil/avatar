/*
 * SDF JSON (Gson) Support
 * -----------------------
 * Drop-in support for serializing & deserializing your SignedDistanceFunction tree.
 *
 * What you get:
 *  - Polymorphic (de)serialization of your SDF node hierarchy using a "class" discriminator.
 *  - Adapters for common math types (JOML Vector3f, Quaternionf, Matrix4f).
 *  - A single entry point (SdfGson) to build a preconfigured Gson instance.
 *
 * How it works:
 *  - Each SDF node is written as a JSON object with a "class" property holding its fully qualified class name.
 *  - On read, we look at "class" and delegate to the matching node type automatically.
 *
 * Assumptions:
 *  - Your SDF API exposes an interface `SignedDistanceFunction` and concrete nodes like:
 *      Sphere, Box, Capsule, Torus, Plane, Union, SmoothUnion, Subtract, Intersect,
 *      Transform (with translation/rotation/scale + child), Repeat (with cell size + child), etc.
 *  - Your math types are JOML (org.joml.*). If you use different types, swap the adapters accordingly.
 *
 * Thread-safety:
 *  - Gson is thread-safe for reads. Builders are not. Build once, reuse the Gson instance.
 *
 * Usage:
 *  Gson gson = SdfGson.create()
 *      // You may register *extra* type adapters here if needed
 *      .create();
 *
 *  // Serialize
 *  String json = gson.toJson(mySdfRoot, SignedDistanceFunction.class);
 *
 *  // Deserialize
 *  SignedDistanceFunction root = gson.fromJson(json, SignedDistanceFunction.class);
 *
 * Notes:
 *  - This uses runtime class names. If you prefer stable short labels, you can pre-register them
 *    via RuntimeClassNameTypeAdapterFactory.registerSubtype(Rectangle.class, "Rect") and write
 *    that label instead. This file defaults to FQCN for zero-config.
 */

package com.amuzil.av3.entity.renderer.sdf;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

// -----------------------------------------------------------------------------
// 1) Polymorphic TypeAdapterFactory using a "class" discriminator (FQCN by default)
//    Adapted and specialized from the StackOverflow pattern the user provided.
// -----------------------------------------------------------------------------
public final class RuntimeClassNameTypeAdapterFactory<T> implements TypeAdapterFactory {
    private final Class<?> baseType;
    private final String typeFieldName;
    private final Map<String, Class<?>> labelToSubtype = new LinkedHashMap<>();
    private final Map<Class<?>, String> subtypeToLabel = new LinkedHashMap<>();

    private RuntimeClassNameTypeAdapterFactory(Class<?> baseType, String typeFieldName) {
        if (typeFieldName == null || baseType == null) {
            throw new NullPointerException("baseType and typeFieldName must be non-null");
        }
        this.baseType = baseType;
        this.typeFieldName = typeFieldName;
    }

    /** Create a runtime adapter that uses a custom discriminator name. */
    public static <T> RuntimeClassNameTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName) {
        return new RuntimeClassNameTypeAdapterFactory<>(baseType, typeFieldName);
    }

    /** Create a runtime adapter that uses "class" as the discriminator. */
    public static <T> RuntimeClassNameTypeAdapterFactory<T> of(Class<T> baseType) {
        return new RuntimeClassNameTypeAdapterFactory<>(baseType, "class");
    }

    /**
     * Optional: register a subtype with a short label instead of a fully-qualified class name.
     * If you do this, that label will be written/read instead of FQCN.
     */
    public RuntimeClassNameTypeAdapterFactory<T> registerSubtype(Class<? extends T> type, String label) {
        if (type == null || label == null) throw new NullPointerException();
        if (subtypeToLabel.containsKey(type) || labelToSubtype.containsKey(label)) {
            throw new IllegalArgumentException("types and labels must be unique");
        }
        labelToSubtype.put(label, type);
        subtypeToLabel.put(type, label);
        return this;
    }

    /** Convenience overload: label = simple class name. */
    public RuntimeClassNameTypeAdapterFactory<T> registerSubtype(Class<? extends T> type) {
        return registerSubtype(type, type.getSimpleName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
        // Only participate for requested base or its subtypes
        if (!baseType.isAssignableFrom(type.getRawType())) return null;

        final Map<String, TypeAdapter<?>> labelToDelegate = new LinkedHashMap<>();
        final Map<Class<?>, TypeAdapter<?>> subtypeToDelegate = new LinkedHashMap<>();

        // If the caller registered short labels, pre-wire their delegates:
        for (Map.Entry<String, Class<?>> e : labelToSubtype.entrySet()) {
            TypeAdapter<?> delegate = gson.getDelegateAdapter(this, TypeToken.get(e.getValue()));
            labelToDelegate.put(e.getKey(), delegate);
            subtypeToDelegate.put(e.getValue(), delegate);
        }

        // We also allow FQCN fallback without explicit registration:
        return new TypeAdapter<R>() {
            @Override
            public void write(JsonWriter out, R value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }
                Class<?> srcType = value.getClass();
                TypeAdapter<R> delegate = getDelegate(srcType);
                if (delegate == null) {
                    // Get a delegate on the fly for this exact class
                    delegate = (TypeAdapter<R>) gson.getDelegateAdapter(RuntimeClassNameTypeAdapterFactory.this, TypeToken.get(srcType));
                    if (delegate == null) {
                        throw new JsonParseException("No delegate for " + srcType.getName());
                    }
                    subtypeToDelegate.put(srcType, delegate);
                }

                JsonElement jsonTree = delegate.toJsonTree(value);
                if (!jsonTree.isJsonObject()) {
                    Streams.write(jsonTree, out);
                    return;
                }

                JsonObject obj = jsonTree.getAsJsonObject();
                if (obj.has(typeFieldName)) {
                    throw new JsonParseException("Cannot serialize " + srcType.getName()
                            + " because it already defines a field named " + typeFieldName);
                }

                String label = subtypeToLabel.getOrDefault(srcType, srcType.getName());
                JsonObject withType = new JsonObject();
                withType.addProperty(typeFieldName, label);
                for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                    withType.add(e.getKey(), e.getValue());
                }
                Streams.write(withType, out);
            }

            @Override
            public R read(JsonReader in) throws IOException {
                JsonElement element = Streams.parse(in);
                if (element == null || element.isJsonNull()) return null;

                if (!element.isJsonObject()) {
                    // Primitive/array => let base delegate try
                    TypeAdapter<R> baseDelegate = gson.getDelegateAdapter(RuntimeClassNameTypeAdapterFactory.this, type);
                    return baseDelegate.fromJsonTree(element);
                }

                JsonObject obj = element.getAsJsonObject();
                JsonElement typeElem = obj.remove(typeFieldName);
                if (typeElem == null) {
                    throw new JsonParseException("Cannot deserialize " + baseType.getName()
                            + ": missing discriminator field '" + typeFieldName + "'");
                }
                String label = typeElem.getAsString();

                TypeAdapter<R> delegate = (TypeAdapter<R>) labelToDelegate.get(label);
                if (delegate == null) {
                    // Not a short label; try FQCN
                    try {
                        Class<?> sub = Class.forName(label);
                        if (!baseType.isAssignableFrom(sub)) {
                            throw new JsonParseException("Type " + label + " is not a subtype of " + baseType.getName());
                        }
                        delegate = (TypeAdapter<R>) gson.getDelegateAdapter(RuntimeClassNameTypeAdapterFactory.this, TypeToken.get(sub));
                    } catch (ClassNotFoundException e) {
                        throw new JsonParseException("Cannot find class " + label, e);
                    }
                }
                return delegate.fromJsonTree(obj);
            }

            @SuppressWarnings("unchecked")
            private TypeAdapter<R> getDelegate(Class<?> srcType) {
                TypeAdapter<?> ta = subtypeToDelegate.get(srcType);
                if (ta != null) return (TypeAdapter<R>) ta;
                // Accept registered supertype match if any:
                for (Map.Entry<Class<?>, TypeAdapter<?>> e : subtypeToDelegate.entrySet()) {
                    if (e.getKey().isAssignableFrom(srcType)) {
                        return (TypeAdapter<R>) e.getValue();
                    }
                }
                return null;
            }
        }.nullSafe();
    }
}

// -----------------------------------------------------------------------------
// 2) Math TypeAdapters (JOML) - small & explicit
// -----------------------------------------------------------------------------
final class Vector3fAdapter extends TypeAdapter<Vector3f> {
    @Override
    public void write(JsonWriter out, Vector3f v) throws IOException {
        if (v == null) { out.nullValue(); return; }
        out.beginObject();
        out.name("x").value(v.x);
        out.name("y").value(v.y);
        out.name("z").value(v.z);
        out.endObject();
    }

    @Override
    public Vector3f read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) { in.nextNull(); return null; }
        float x = 0, y = 0, z = 0;
        in.beginObject();
        while (in.hasNext()) {
            String n = in.nextName();
            switch (n) {
                case "x": x = (float) in.nextDouble(); break;
                case "y": y = (float) in.nextDouble(); break;
                case "z": z = (float) in.nextDouble(); break;
                default: in.skipValue();
            }
        }
        in.endObject();
        return new Vector3f(x, y, z);
    }
}

final class QuaternionfAdapter extends TypeAdapter<Quaternionf> {
    @Override
    public void write(JsonWriter out, Quaternionf q) throws IOException {
        if (q == null) { out.nullValue(); return; }
        out.beginObject();
        out.name("x").value(q.x);
        out.name("y").value(q.y);
        out.name("z").value(q.z);
        out.name("w").value(q.w);
        out.endObject();
    }

    @Override
    public Quaternionf read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) { in.nextNull(); return null; }
        float x = 0, y = 0, z = 0, w = 1;
        in.beginObject();
        while (in.hasNext()) {
            String n = in.nextName();
            switch (n) {
                case "x": x = (float) in.nextDouble(); break;
                case "y": y = (float) in.nextDouble(); break;
                case "z": z = (float) in.nextDouble(); break;
                case "w": w = (float) in.nextDouble(); break;
                default: in.skipValue();
            }
        }
        in.endObject();
        return new Quaternionf(x, y, z, w);
    }
}

final class Matrix4fAdapter extends TypeAdapter<Matrix4f> {
    @Override
    public void write(JsonWriter out, Matrix4f m) throws IOException {
        if (m == null) { out.nullValue(); return; }
        // Row-major 4x4
        float[] a = new float[16];
        m.get(a);
        out.beginArray();
        for (int i = 0; i < 16; i++) out.value(a[i]);
        out.endArray();
    }

    @Override
    public Matrix4f read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) { in.nextNull(); return null; }
        float[] a = new float[16];
        int i = 0;
        in.beginArray();
        while (in.hasNext() && i < 16) {
            a[i++] = (float) in.nextDouble();
        }
        in.endArray();
        Matrix4f m = new Matrix4f();
        if (i == 16) m.set(a);
        else throw new JsonParseException("Matrix4f expects 16 floats, got " + i);
        return m;
    }
}

// -----------------------------------------------------------------------------
// 3) SDF Gson bootstrapper
//    - Registers polymorphism for SignedDistanceFunction
//    - Registers math adapters
//    - Optional: also register short labels for frequently used nodes
// -----------------------------------------------------------------------------
final class SdfGson {
    private SdfGson() {}

    /**
     * Build a GsonBuilder preconfigured for your SDF API.
     * You can further customize (prettyPrinting, your own adapters, etc.) before calling .create().
     *
     * Replace the class names below with your actual SDF node classes if they differ.
     */
    public static GsonBuilder create() {
        GsonBuilder gb = new GsonBuilder();

        // === Math types ===
        gb.registerTypeAdapter(Vector3f.class, new Vector3fAdapter());
        gb.registerTypeAdapter(Quaternionf.class, new QuaternionfAdapter());
        gb.registerTypeAdapter(Matrix4f.class, new Matrix4fAdapter());

        // === Polymorphic SDF ===
        // NOTE: replace package/class names if your nodes differ.
        @SuppressWarnings("unchecked")
        RuntimeClassNameTypeAdapterFactory<com.amuzil.av3.entity.renderer.sdf.SignedDistanceFunction> sdfFactory =
                RuntimeClassNameTypeAdapterFactory.of(com.amuzil.av3.entity.renderer.sdf.SignedDistanceFunction.class)
                        .registerSubtype(com.amuzil.av3.entity.renderer.sdf.shapes.SDFSphere.class, "Sphere")
                        .registerSubtype(com.amuzil.av3.entity.renderer.sdf.shapes.SDFBox.class, "Box")
                        .registerSubtype(com.amuzil.av3.entity.renderer.sdf.shapes.SDFCapsule.class, "Capsule")
                        .registerSubtype(com.amuzil.av3.entity.renderer.sdf.shapes.SDFTorus.class, "Torus")
                        .registerSubtype(com.amuzil.av3.entity.renderer.sdf.shapes.SDFPlane.class, "Plane")
                        .registerSubtype(com.amuzil.av3.entity.renderer.sdf.operators.SDFUnion.class, "Union")
                        .registerSubtype(com.amuzil.av3.entity.renderer.sdf.operators.SDFSmoothUnion.class, "SmoothUnion")
                        .registerSubtype(com.amuzil.av3.entity.renderer.sdf.operators.SDFSubtract.class, "Subtract")
                        .registerSubtype(com.amuzil.av3.entity.renderer.sdf.operators.SDFIntersect.class, "Intersect");

        RuntimeClassNameTypeAdapterFactory<com.amuzil.av3.entity.renderer.sdf.channels.floats.IFloatChannel> floatChannelFactory =
                RuntimeClassNameTypeAdapterFactory.of(com.amuzil.av3.entity.renderer.sdf.channels.floats.IFloatChannel.class)
                        .registerSubtype(com.amuzil.av3.entity.renderer.sdf.channels.floats.ConstantFloatChannel.class, "ConstantFloat")
                        .registerSubtype(com.amuzil.av3.entity.renderer.sdf.channels.floats.PulsingFloatChannel.class, "PulsingFloat");

        RuntimeClassNameTypeAdapterFactory<com.amuzil.av3.entity.renderer.sdf.channels.quaternions.IQuatChannel> quatChannelFactory =
                RuntimeClassNameTypeAdapterFactory.of(com.amuzil.av3.entity.renderer.sdf.channels.quaternions.IQuatChannel.class)
                        .registerSubtype(com.amuzil.av3.entity.renderer.sdf.channels.quaternions.ConstantQuaternionChannel.class, "ConstantQuat")
                        .registerSubtype(com.amuzil.av3.entity.renderer.sdf.channels.quaternions.SpinYChannel.class, "SpinY");

        RuntimeClassNameTypeAdapterFactory<com.amuzil.av3.entity.renderer.sdf.channels.vectors.IVec3Channel> vec3ChannelFactory =
                RuntimeClassNameTypeAdapterFactory.of(com.amuzil.av3.entity.renderer.sdf.channels.vectors.IVec3Channel.class)
                        .registerSubtype(com.amuzil.av3.entity.renderer.sdf.channels.vectors.ConstantVectorChannel.class, "ConstantVector")
                        .registerSubtype(com.amuzil.av3.entity.renderer.sdf.channels.vectors.OrbitXZChannel.class, "OrbitXZ");

        gb.registerTypeAdapterFactory(sdfFactory);
        gb.registerTypeAdapterFactory(floatChannelFactory);
        gb.registerTypeAdapterFactory(quatChannelFactory);
        gb.registerTypeAdapterFactory(vec3ChannelFactory);

        // Good defaults
        gb.disableHtmlEscaping();
        gb.serializeNulls();
        gb.setPrettyPrinting();

        return gb;
    }
}

// -----------------------------------------------------------------------------
// 4) OPTIONAL: If you want very compact JSON for vectors/quaternions, you can
//    flip the above adapters to use arrays instead of objects. Example below.
//    (Commented out; keep or swap as desired.)
/*
final class Vector3fArrayAdapter extends TypeAdapter<Vector3f> {
    @Override public void write(JsonWriter out, Vector3f v) throws IOException {
        if (v == null) { out.nullValue(); return; }
        out.beginArray(); out.value(v.x); out.value(v.y); out.value(v.z); out.endArray();
    }
    @Override public Vector3f read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) { in.nextNull(); return null; }
        in.beginArray();
        float x = (float) in.nextDouble();
        float y = (float) in.nextDouble();
        float z = (float) in.nextDouble();
        in.endArray();
        return new Vector3f(x,y,z);
    }
}
*/
// -----------------------------------------------------------------------------
// End of file
// -----------------------------------------------------------------------------
