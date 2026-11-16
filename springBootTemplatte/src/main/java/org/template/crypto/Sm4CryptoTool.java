package org.template.crypto;

/**
 * 运维离线加密小工具（Sm4CryptoTool）。
 *
 * <p>用途：运维在安全终端上用密钥材料加密数据库用户名/口令，输出 {@code SM4(...)} 回填
 * {@code application.yml}，开发人员不接触密钥明文，满足监管第 1 条。
 * <p>用法：
 * <pre>
 *   java -cp app.jar org.template.crypto.Sm4CryptoTool "DbPass123"        // 密钥来自环境变量 SM4_KEY
 *   java -cp app.jar org.template.crypto.Sm4CryptoTool --key-file /run/secrets/sm4.key "DbPass123"
 *   java -cp app.jar org.template.crypto.Sm4CryptoTool --key "口令材料" "user1" "pass1"
 * </pre>
 * 注意：命令行参数会被 shell 历史记录下来，生产建议用环境变量 / 文件方式提供密钥，
 * 待加密明文用交互输入（不回显仍有风险，以运维保险柜流程为准）。
 */
public class Sm4CryptoTool {

    public static void main(String[] args) throws Exception {
        String keyFile = null;
        String keyMaterial = null;
        java.util.List<String> plains = new java.util.ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if ("--key-file".equals(args[i]) && i + 1 < args.length) {
                keyFile = args[++i];
            } else if ("--key".equals(args[i]) && i + 1 < args.length) {
                keyMaterial = args[++i];
            } else if ("--key-env".equals(args[i]) && i + 1 < args.length) {
                String envName = args[++i];
                keyMaterial = System.getenv(envName);
            } else {
                plains.add(args[i]);
            }
        }
        if (plains.isEmpty()) {
            // 交互输入，避免明文留在 shell 历史中
            System.out.print("请输入待加密明文: ");
            char[] input = System.console() == null
                    ? new java.util.Scanner(System.in).nextLine().toCharArray()
                    : System.console().readPassword();
            plains.add(new String(input));
            java.util.Arrays.fill(input, '\0');
        }

        byte[] material;
        String source;
        if (keyMaterial != null) {
            material = keyMaterial.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            source = "命令行 --key（用后请清理历史）";
        } else if (keyFile != null) {
            material = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(keyFile));
            source = "密钥文件 " + keyFile;
        } else {
            String env = System.getenv("SM4_KEY");
            if (env == null || env.isEmpty()) {
                System.err.println("未找到密钥：请设置环境变量 SM4_KEY，或使用 --key-file / --key");
                System.exit(2);
                return;
            }
            material = env.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            source = "环境变量 SM4_KEY";
        }
        try {
            Sm4KeyManager manager = new Sm4KeyManager(
                    java.util.Collections.singletonList(() -> material), false, false);
            Sm4CipherService cipher = new Sm4CipherService(manager);
            System.out.println("密钥来源: " + source + "（SM3 派生 16 字节 SM4 密钥，不打印密钥内容）");
            for (String plain : plains) {
                System.out.println(cipher.encrypt(plain));
            }
            manager.destroy();
        } finally {
            java.util.Arrays.fill(material, (byte) 0);
        }
    }
}
