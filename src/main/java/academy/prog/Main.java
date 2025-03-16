package academy.prog;

import academy.prog.models.Message;
import academy.prog.models.User;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	public static String FindName(String message) {
		Pattern pattern = Pattern.compile("@([a-zA-Z0-9_]+)");
		Matcher matcher = pattern.matcher(message);

		if (matcher.find()) {
			return matcher.group(1); // find name after @
		}
		return null;
	}
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		try {
			System.out.print("Enter your login: ");
			String login = scanner.nextLine();
			System.out.print("Enter your password (stored in Enum = DefaultPassword on Server): ");
			String pass = scanner.nextLine();

			User usr = new User();
			usr.setLogin(login);
			usr.setPassword(pass);

			int answerUserCheck = usr.send(Utils.getURL() + "/checkusr");
			if (answerUserCheck == 200) { // 200 OK

				System.out.println("User '" + usr.getLogin() + "' logged in successfully!");
				Thread th = new Thread(new GetThread(login));
				th.setDaemon(true);
				th.start();

				System.out.print("\nEnter your message: ");
				while (true) {
					String text = scanner.nextLine();
					if (text.isEmpty()) break;

					String loginTo = FindName(text);

					Message m = new Message(login, loginTo, text);
					int res = m.send(Utils.getURL() + "/addmsg");

					if (res != 200) { // 200 OK
						System.out.println("HTTP error occurred: " + res);
						return;
					}
				}
			}else{
				System.out.println("HTTP error occurred: " + answerUserCheck);
				usr.send(Utils.getURL() + "/addStatus");
				System.out.println("User '" +usr.getLogin() + "' DIDN'T log in!");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			scanner.close();
		}
	}
}
