package org.template.crypto;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * 运维离线加密小工具（Sm4CryptoTool）。
 *
 * <p>用途：运维在安全终端上用密钥材料加密数据库用户名/口令，输出 {@code SM4(...)} 回填
 * {@code application.yml}，开发人员不接触密钥明文，满足监管第 1 条。
 *
 * <p><b>密钥材料的四种来源（优先级从高到低）</b>：
 * <ol>
 *   <li>{@code --sm4-key <材料>}：命令行直接给密钥材料，<b>不依赖环境变量</b>，
 *       适合自动化脚本 / 测试环境 / 无 env 注入的场景；</li>
 *   <li>{@code --key <材料>}：同上的语义别名，保留以兼容既有使用习惯；</li>
 *   <li>{@code --key-file <路径>}：从密钥文件读取（权限应为 600）；</li>
 *   <li>{@code --key-env <变量名>}：从指定环境变量读取；</li>
 *   <li>以上都没给时，回退读环境变量 {@code SM4_KEY}。</li>
 * </ol>
 *
 * <p>用法：
 * <pre>
 *   # 绕过环境变量，直接给密钥材料（推荐用于脚本化）
 *   java -cp "target/classes:$(cat cp.txt)" org.template.crypto.Sm4CryptoTool --sm4-key "my-key-material" "root" "password"
 *
 *   # 密钥来自环境变量 SM4_KEY
 *   export SM4_KEY="my-key-material"
 *   java -cp "target/classes:$(cat cp.txt)" org.template.crypto.Sm4CryptoTool "root" "password"
 *
 *   # 密钥来自文件
 *   java -cp "target/classes:$(cat cp.txt)" org.template.crypto.Sm4CryptoTool --key-file /run/secrets/sm4.key "root"
 * </pre>
 *
 * <p><b>注意</b>：命令行参数会留在 shell 历史记录里，生产环境优先用
 * {@code --key-file} 或环境变量；待加密明文可用交互输入方式（无明文参数）。
 */
public class Sm4CryptoTool {

    /** 回退使用的环境变量名 */
    private static final String DEFAULT_KEY_ENV = "SM4_KEY";

    public static void main(String[] args) throws Exception {
        String keyFile = null;
        String keyMaterial = null;
        String keyEnvName = null;
        List<String> plains = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if ("--key-file".equals(args[i]) && i + 1 < args.length) {
                keyFile = args[++i];
            } else if ("--sm4-key".equals(args[i]) && i + 1 < args.length) {
                // 绕过环境变量，直接指定密钥材料
                keyMaterial = args[++i];
            } else if ("--key".equals(args[i]) && i + 1 < args.length) {
                keyMaterial = args[++i];
            } else if ("--key-env".equals(args[i]) && i + 1 < args.length) {
                keyEnvName = args[++i];
            } else if (("--help".equals(args[i]) || "-h".equals(args[i]))) {
                printUsage();
                return;
            } else {
                plains.add(args[i]);
            }
        }

        if (plains.isEmpty()) {
            // 交互输入，避免明文留在 shell 历史中
            System.out.print("请输入待加密明文: ");
            char[] input = System.console() == null
                    ? new Scanner(System.in).nextLine().toCharArray()
                    : System.console().readPassword();
            plains.add(new String(input));
            Arrays.fill(input, '\0');
        }

        byte[] material;
        String source;
        if (keyMaterial != null && !keyMaterial.isEmpty()) {
            material = keyMaterial.getBytes(StandardCharsets.UTF_8);
            source = "命令行 --sm4-key/--key（用后请清理 shell 历史）";
        } else if (keyFile != null && !keyFile.trim().isEmpty()) {
            material = Files.readAllBytes(Paths.get(keyFile.trim()));
            source = "密钥文件 " + keyFile.trim();
        } else {
            String envName = (keyEnvName == null || keyEnvName.isEmpty()) ? DEFAULT_KEY_ENV : keyEnvName;
            String env = System.getenv(envName);
            if (env == null || env.isEmpty()) {
                System.err.println("未找到密钥材料：请使用 --sm4-key <材料> / --key-file <路径> / --key-env <变量名>，"
                        + "或设置环境变量 " + DEFAULT_KEY_ENV);
                printUsage();
                System.exit(2);
                return;
            }
            material = env.getBytes(StandardCharsets.UTF_8);
            source = "环境变量 " + envName;
        }

        try {
            Sm4KeyManager manager = new Sm4KeyManager(
                    Collections.singletonList(() -> material), false, false);
            Sm4CipherService cipher = new Sm4CipherService(manager);
            System.out.println("密钥来源: " + source + "（SM3 派生 16 字节 SM4 密钥，不打印密钥内容）");
            for (String plain : plains) {
                System.out.println(cipher.encrypt(plain));
            }
            manager.destroy();
        } finally {
            Arrays.fill(material, (byte) 0);
        }
    }

    private static void printUsage() {
        System.err.println();
        System.err.println("用法: java -cp <classpath> org.template.crypto.Sm4CryptoTool [选项] [待加密明文...]");
        System.err.println("  --sm4-key <材料>   直接指定密钥材料（不依赖环境变量）");
        System.err.println("  --key <材料>       同 --sm4-key 的别名");
        System.err.println("  --key-file <路径>  从密钥文件读取材料");
        System.err.println("  --key-env <变量名> 从指定环境变量读取材料（默认 " + DEFAULT_KEY_ENV + "）");
        System.err.println("  不带明文参数时进入交互输入模式");
        System.err.println();
        System.err.println("示例:");
        System.err.println("  java -cp \"target/classes:$CP\" org.template.crypto.Sm4CryptoTool --sm4-key \"my-key\" root password");
    }
}
