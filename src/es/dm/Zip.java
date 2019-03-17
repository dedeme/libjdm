// Copyright 17-Mar-2019 ºDeme
// GNU General Public License - V3 <http://www.gnu.org/licenses/>

package es.dm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Zip compressor.
 *
 * @version 1.0
 * @since 07-Apr-2014
 * @author deme
 */
public class Zip {

    private Zip() {
    }

    static void compressFile(
            File dirBase, File source, ZipOutputStream zip,
            FileFilter ff, byte[] bf)
            throws IOException {

        int buffer = bf.length;
        File absSource = new File(dirBase, source.toString());
        if (ff.accept(absSource)) {
            String nameZip = source.toString().replace(File.separator, "/");
            ZipEntry entry = new ZipEntry(nameZip);
            zip.putNextEntry(entry);
            try (BufferedInputStream in = new BufferedInputStream(
                    new FileInputStream(absSource), buffer)) {
                int n;
                while ((n = in.read(bf, 0, buffer)) != -1) {
                    zip.write(bf, 0, n);
                }

                zip.closeEntry();
            }
        }
    }

    static void compressDir(
            File dirBase, File source, ZipOutputStream zip, FileFilter ff, byte[] bf)
            throws IOException {

        File absSource = new File(dirBase, source.toString());
        if (ff.accept(absSource)) {
            String nameZip = source.toString().replace(File.separator, "/");
            ZipEntry entry = new ZipEntry(nameZip + "/");
            zip.putNextEntry(entry);
            zip.closeEntry();

            for (String f : absSource.list()) {
                File newSource = new File(source, f);
                if (new File(absSource, f).isDirectory()) {
                    compressDir(dirBase, newSource, zip, ff, bf);
                } else {
                    compressFile(dirBase, newSource, zip, ff, bf);
                }
            }
        }
    }

    /**
     * Compresses source to target.
     *
     * @param source Can be a file or a directory.
     * @param target Complete name of file .zip. (e.g. "com.zip")
     * @param ff Selector of files. If its value is null it selects all files.
     * @param buffer Buffer size
     * @param charset Charset
     * @throws java.io.FileNotFoundException Exception
     */
    public static void zip(
            File source, File target, FileFilter ff, int buffer,
            Charset charset)
            throws FileNotFoundException, IOException {

        if (ff == null) {
            ff = (File pathname) -> true;
        }
        byte bf[] = new byte[buffer];
        try (ZipOutputStream zip
                = new ZipOutputStream(new FileOutputStream(target), charset)) {
            if (source.isDirectory()) {
                compressDir(source.getParentFile(), new File(source.getName()),
                        zip, ff, bf);
            } else {
                compressFile(source.getParentFile(), new File(source.getName()),
                        zip, ff, bf);
            }
        }
    }

    /**
     * Compresses source to target using UTF-8 as default charset.
     *
     * @param source Can be a file or a directory.
     * @param target Complete name of file .zip. (e.g. "com.zip")
     * @param ff Selector of files. If its value is null it selects all files.
     * @param buffer Buffer size
     * @throws java.io.FileNotFoundException Exception
     */
    public static void zip(
            File source, File target, FileFilter ff, int buffer)
            throws FileNotFoundException, IOException {
        zip(source, target, ff, buffer, StandardCharsets.UTF_8);
    }

    /**
     * Compresses source to target using a buffer of 4096 bytes and UTF-8 as
     * default charset.
     *
     * @param source Can be a file or a directory.
     * @param target Complete name of file .zip. (e.g. "com.zip")
     * @param ff Selector of files. If its value is null it selects all files.
     * @throws java.io.FileNotFoundException Exception
     */
    public static void zip(File source, File target, FileFilter ff)
            throws FileNotFoundException, IOException {
        zip(source, target, ff, 4096);
    }

    /**
     * Compresses source to target using a buffer of 4096 bytes, UTF-8 as
     * default charset and compressing all files.
     *
     * @param source Can be a file or a directory.
     * @param target Complete name of file .zip. (e.g. "com.zip")
     * @throws java.io.FileNotFoundException Exception
     */
    public static void zip(File source, File target)
            throws FileNotFoundException, IOException {
        zip(source, target, null);
    }

    /**
     * Unzip source in the 'target' directory.
     *
     * @param source Zip file with complete name.
     * @param target Directory to uncompress.
     * @param buffer Buffer size.
     * @param charset Charset
     * @throws java.io.FileNotFoundException Exception
     */
    public static void unzip(
            File source, File target, int buffer, Charset charset)
            throws FileNotFoundException, IOException {
        byte[] bf = new byte[buffer];

        BufferedOutputStream out;
        try (ZipInputStream zip = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(source)),
                charset)) {
            ZipEntry zipEntry;
            while ((zipEntry = zip.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    File dir = new File(target, zipEntry.getName());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                } else {
                    File targetFile = new File(target, zipEntry.getName());

                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }

                    FileOutputStream fo = new FileOutputStream(targetFile);
                    out = new BufferedOutputStream(fo, buffer);
                    int n;
                    while ((n = zip.read(bf, 0, buffer)) != -1) {
                        out.write(bf, 0, n);
                    }

                    out.flush();
                    out.close();
                }
            }
        }
    }

    /**
     * Unzip source encoded in UTF-8 into the 'target' directory.
     *
     * @param source Zip file with complete name.
     * @param target Directory to uncompress.
     * @param buffer Buffer size.
     * @throws java.io.FileNotFoundException Exception
     */
    public static void unzip(File source, File target, int buffer)
            throws FileNotFoundException, IOException {
        unzip(source, target, buffer, StandardCharsets.UTF_8);
    }

    /**
     * Unzip source in the 'target' directory, using a buffer of 4096 bytes and
     * encoded with UTF-8.
     *
     * @param source Zip file with complete name.
     * @param target Directory to uncompress.
     * @throws java.io.FileNotFoundException Exception
     */
    public static void unzip(File source, File target)
            throws FileNotFoundException, IOException {
        unzip(source, target, 4096);
    }
}
