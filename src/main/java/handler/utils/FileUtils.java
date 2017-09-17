package handler.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;

public class FileUtils {

    public static void CopyFileOrFolder(File source, File dest, CopyOption... options) throws IOException {
        if(source.isDirectory()) {
            CopyFolder(source, dest, options);
        }else{
            EnsureParentFolder(dest);
            CopyFile(source, dest, options);
        }
    }

    private static void CopyFolder(File source, File dest, CopyOption... options) throws IOException {
        if(!dest.exists())
            dest.mkdirs();

        File[] contents = source.listFiles();
        if(contents == null) return;
        for (File f : contents) {
            File newFile = new File(dest.getAbsolutePath() + File.separator + f.getName());
            if(f.isDirectory())
                CopyFolder(f, newFile, options);
            else CopyFile(f, newFile, options);
        }
    }

    private static void CopyFile(File source, File dest, CopyOption... options) throws IOException {
        Files.copy(source.toPath(), dest.toPath(), options);
    }

    private static void EnsureParentFolder(File file) {
        File parent = file.getParentFile();
        if(parent != null && !parent.exists())
            parent.mkdirs();
    }

    public static boolean IsJar(File file) {
        return file.getPath().toLowerCase().endsWith(".jar");
    }

}
