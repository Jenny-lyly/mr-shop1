package com.baidu.shop.config;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @ClassName AlipayConfig
 * @Description: AlipayConfig
 * @Author jinluying
 * @create: 2020-10-22 19:02
 * @Version V1.0
 **/
public class AlipayConfig {

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016102600766670";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCzy7U8KanWNQ2+II0xs2ThgONiFSih+vsZvz3l2E1nao/3wH9b/FFARqZKAGXsMEnLHMFESF5hvfPZ846PcBktWGTGoWscmalaNjD1vb3SYGodGnnpH36aIAstkAUwO4/Nn7pVWsHonUW5sCVLzjEGbRji0KiAo/8MPcnCRwcYR83dqOdXDAWXfq+tuJvyMfjGVPaPGmxg8TfFJNWXShwLLwqedsCpbsknhvBX+yRNa+DZq8uNZrOJ6kVqB++7rSJgshW/oSS0hSZy0sKODdF/5V26rCrPVlCHdNzip7frnPZ+yW9KOmYV0EuEcqv3IzohdMpn62eaMHpXWyVbg02hAgMBAAECggEBALJgnEJAI5u9rxC+QtbYYHHJ6c3ZqCJsWstEjSiH3OOJOaRXewPL5OCcN0ab9oLJtayprqUMF3dpeZD2rq1oTdJLLFBUvWv2l6pEsXZc9QLPXPfa2LCM6pIG0JA3nq4nqZgbrEYacN9TiWWceKQowULa4iHFWiemjNecRZtV1JgkmlmZpB3x6j8PPDZz7Q15EFxl9Aq+q90wcWZ8QQDHNsD6FUVlCxpJE5/5cBgCa8sansLvMrOwblEU8sihOnxHvcVg24XUZTUuIerlQnbD2K2oBvILo7oJpsc6tOoOVsXUZBS2ALbdDlVmMjtRnvg7PVdrVuNzLPdvx5eL2+10e7ECgYEA5UbDNpZfZYUxKd53kyGq16BjZm9yvJ5O5V+eKb9o6xcoIw0zzTKrBF8iCdml8fCrxdapB1Rw044Hf3kkYmFQH0SSI7QCCRe2WVlKy48f0xlucsBg69Nzy5dqRdHqxoQWFcXmziFIWomPZDYTguG6QJTTm/eZhoBvKKzd0crcY0MCgYEAyMCFmVnrepG85wmKYSUrnt3iAFEYoMyeI1/RgzTwjGAb664StbsBIpBe6lyu20ezQ0KU8TjGXjjSVO+Iz1XZsFw2fpg6mf33S5+G329qab1RRoCzk4CQkvNn4vj5yzpoxiIwzU0NDMhJYMGnrPVeLjgiMCkAD8U2KieF5V0200sCgYEAvFr15NKEbJKPYSYydd0izSImhvDoAazMhAOZnBJzzy6m+qTN+bW2aVD+VblIawqdE1rWYf8s8Z+s6TxMd8YaXJWouUwYn/+q/pwspi5epr4tznfdJ9//MTKjS9i4V34L5rc+dflBx59a/aivPsbSjFu3vFoc9jJtH61kTgWqKKECgYAJw/O7xfgCelxqELi+iQRaMgjwYwGDsPBwYBTdARJCgdpZ2JnoZTRk7gjv9Zo76Hz75J1CLrTXmIQCxbQuAcsZHSM5fUY8f3IsJ5J0BAN1KVRuzYvuvO5Ld7OyAtKD5E98c+VT10+vM37+Se1SKI1wSiK/nP73xWwhGNMWmTY1iQKBgAd3GyM/Sx5GUuxfh/ffeTFfPrPlBGnPPv/69jk7fz5rZ9bweuNCn4GHBRqm2aApBDqijDSg7gJHqgK/w7wnLUmsObxHKMAIEyJfsgq2Vu4f5r2B+UJatWLihtPcySqZYsc0+7LBzp53NA2uU3xKtP2tBc8fBC8I0pFDuPrj6oIL";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkIoBZJjQqJvgvgneeNWfER7C/dYVj0QH5ThcW70HudZq3TPtCC7MsTeSgnT05jWKZR6TSuut2oI1V4Ssl8WEDFhOFGUNK1rYNf7SaFTWcWTcTb3Djh2mqz/JJ/1jlbGhXF0v6s6iXONEpv6SAHVof7K+e7q9qxktRYSw6Q7Ix+8RPXEZmbVWSWqB5X9m5sCblN2ynK0KafN+u8398alpQIhKS7OPdFFL44FXyff7wmBz/8XC3LHSlnQRjJ3OP75SJKxhGQU/3a68bmxqTslJ5R07QHdVCYxIwIXKKLG0i4aQKdE36cQC33K0ECh41FFE29+qI4kHuNaXVcHcHh7ODwIDAQAB";
    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8900/pay/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8900/pay/return";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "D:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
