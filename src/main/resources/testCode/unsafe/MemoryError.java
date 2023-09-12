

import java.util.ArrayList;

/**
 * 无限占用空间
 *
 * @author 蒙宇潮
 * @create 2023-09-11  15:28
 */
public class Main {
    public static void main(String[] args) {
        long totalMem = Runtime.getRuntime().totalMemory();
        System.out.println("jvm总内存" + totalMem);
        ArrayList<byte[]> bytes = new ArrayList<>();
        while (true) {
            bytes.add(new byte[10000]);
        }
    }
}
