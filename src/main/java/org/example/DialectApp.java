package org.example;
import cn.xfyun.config.SpeechRecognitionService;
import java.util.Scanner;

public class DialectApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SpeechRecognitionService recognitionService = new SpeechRecognitionService();

        while (true) {
            System.out.println("\n======================================");
            System.out.println("  欢迎使用方言语音识别系统");
            System.out.println("======================================");
            System.out.println("请选择您要使用的方言种类：");
            System.out.println("  1: 普通话 (mandarin)");
            System.out.println("  2: 粤语 (cn_cantonese)");
            System.out.println("  0: 退出程序");
            System.out.print("请输入数字选择: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            String accent = "";

            switch (choice) {
                case 1: accent = "mandarin"; break;
                case 2: accent = "cn_cantonese"; break;
                case 0:
                    System.out.println("感谢使用，程序已退出。");
                    scanner.close();
                    return;
                default:
                    System.out.println("无效的选择，请重新输入。");
                    continue;
            }

            recognitionService.startListening(accent, (finalResult) -> {
                System.out.println("\n----------- 最终识别结果 -----------");
                System.out.println("【" + finalResult + "】");
                System.out.println("------------------------------------");
                System.out.println("\n(识别已自动结束，您可以按回车返回主菜单)");
            });

            System.out.println("正在聆听... 按回车键可手动停止本次识别，并返回主菜单...");
            scanner.nextLine();

            // 关键：在用户按回车后，调用stopListening()来释放资源
            recognitionService.stopListening();
        }
    }
}