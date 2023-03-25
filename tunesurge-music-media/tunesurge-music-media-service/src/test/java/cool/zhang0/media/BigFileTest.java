package cool.zhang0.media;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.util.*;

/**
 * <大文件测试>
 *
 * @Author zhanglin
 * @createTime 2023/3/22 19:33
 */
public class BigFileTest {

    @Test
    void testChunk() throws IOException {
        // 源文件
        File sourceFile = new File("D:\\Upload\\对不起.avi");

        // 分块文件存储路径
        File chunkFolderPath = new File("D:\\Upload\\bigfile_test\\chunk\\");
        if (!chunkFolderPath.exists()) {
            chunkFolderPath.mkdirs();
        }

        // 分块的大小
        int chunkSize = 1024 * 1024 * 1;
        // 分块的数量
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize);

        // 使用流对象读取源文件 =》 向分块文件写数据 =》 达到分块大小不再写
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
        // 缓冲区
        byte[] b = new byte[1024];
        for (long i = 0; i < chunkNum; i++) {
            File file = new File("D:\\Upload\\bigfile_test\\chunk\\" + i);
            // 如果分块文件存在，则删除
            if (file.exists()) {
                file.delete();
            }
            boolean newFile = file.createNewFile();
            if (newFile) {
                // 向分块文件写数据流对象
                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                int len = -1;
                while ((len = raf_read.read(b)) != -1) {
                    raf_write.write(b, 0, len);
                    if (file.length() >= chunkSize) {
                        break;
                    }
                }
                raf_write.close();
            }
        }

    }

    @Test
    void testMerge() throws IOException {
        // 源文件
        File sourceFile = new File("D:\\Upload\\对不起.avi");

        // 分块文件存储路径
        File chunkFolderPath = new File("D:\\Upload\\bigfile_test\\chunk\\");
        if (!chunkFolderPath.exists()) {
            chunkFolderPath.mkdirs();
        }

        File mergeFile = new File("D:\\Upload\\bigfile_test\\合并.avi");
        boolean newFile = mergeFile.createNewFile();

        // 流对象读取分块文件 =》 按顺序依次向合并文件写数据
        // 获取分块文件列表 按文件名升序排序
        File[] chunkFiles = chunkFolderPath.listFiles();
        List<File> chunkFileList = Arrays.asList(chunkFiles);

        // 按文件名升序排序
        Collections.sort(chunkFileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });

        // 创建合并文件的流对象
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        byte[] b = new byte[1024];
        for (File file : chunkFileList) {
            RandomAccessFile raf_read = new RandomAccessFile(file, "r");
            int len = -1;
            while ((len = raf_read.read(b)) != -1) {
                // 每次将b缓冲区的数据从0处到len长度结尾写入raf_write中
                raf_write.write(b, 0, len);
            }
        }

        // 校验合并后的文件是否正确
        FileInputStream sourceFileStream = new FileInputStream(sourceFile);
        FileInputStream mergeFileStream = new FileInputStream(mergeFile);
        String sourceMd5Hex = DigestUtils.md5DigestAsHex(sourceFileStream);
        String mergeMd5Hex = DigestUtils.md5DigestAsHex(mergeFileStream);
        System.out.println(sourceMd5Hex);
        System.out.println(mergeMd5Hex);
        System.out.println(sourceMd5Hex.equals(mergeMd5Hex));

    }

}
