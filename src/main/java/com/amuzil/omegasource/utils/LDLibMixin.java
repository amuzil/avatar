package com.amuzil.omegasource.utils;


import com.amuzil.av3.Avatar;
import com.lowdragmc.lowdraglib2.LDLib2;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.file.Path;

//@Mixin(LDLib2.class)
//public class LDLibMixin {
//    @Unique
//    private static final File cosmic_collapse$assetsDir = cosmic_collapse$getAssetsFile();
//
//    @Inject(method = "getAssetsDir", at = @At("HEAD"), cancellable = true)
//    private static void getAssetsDir(CallbackInfoReturnable<File> cir) {
//        if (!FMLLoader.isProduction()) {
//            cir.setReturnValue(cosmic_collapse$assetsDir);
//        }
//    }
//
//    // Note: this will only work in a dev environment since you obviously cant access a directory inside of the built jar
//    @Unique
//    private static File cosmic_collapse$getAssetsFile() {
//        String modPath = ModList.get().getModFileById(Avatar.MOD_ID).getFile().getFilePath().toAbsolutePath().toString();
//        String srcPath = modPath.replace("build\\resources\\main", "src\\main\\resources\\assets");
//        return Path.of(srcPath).toFile();
//    }
//}
