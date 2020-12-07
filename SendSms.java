///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 11+
//DEPS info.picocli:picocli:4.5.2
//DEPS info.picocli:picocli-codegen:4.5.2
//DEPS com.twilio.sdk:twilio:8.4.0

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.util.Scanner;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(
	name = "SendSms",
	version = {
			"SendSms 0.1",
			"Picocli " + picocli.CommandLine.VERSION,
			"JVM: ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})",
			"OS: ${os.name} ${os.version} ${os.arch}"},
	description = "SendSms made with jbang",
	mixinStandardHelpOptions = true)
class SendSms implements Callable<Integer> {

	@CommandLine.Option(
		names = {"-t", "--to"},
		description = "The number you're sending the message @|bold to|@",
		required = true)
	private String toPhoneNumber;

	@CommandLine.Option(
		names = {"-f", "--from"},
		description = "The number you're sending the message @|bold from|@",
		required = true)
	private String fromPhoneNumber;

	@CommandLine.Parameters(
		index = "0",
		description = "The message to send",
		arity = "0..*")
	private String[] message;

	public static void main(String... args) {
		int exitCode = new CommandLine(new SendSms()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() throws Exception { // your business logic goes here...
		String wholeMessage;
		if (message != null) {
			wholeMessage = String.join(" ", message);
		} else {
			var scanner = new Scanner(System.in).useDelimiter("\\A");
			wholeMessage = "";
			if (scanner.hasNext()) {
				wholeMessage = scanner.next();
			}
		}

		if (wholeMessage.isBlank()) {
			printlnAnsi("@|red You need to provide a message somehow|@");
			return 1;
		}

		try {
			System.out.println("Sending to..." + toPhoneNumber + ": ");
			sendSMS(toPhoneNumber, fromPhoneNumber, wholeMessage);
			printlnAnsi("@|green OK|@");
		} catch (Exception e) {
			printlnAnsi("@|red FAILED|@");
			printlnAnsi("@|red " + e.getMessage() + "|@");
			return 1;
		}

		return 0;
	}

	private static void printlnAnsi(String msg) {
		System.out.println(CommandLine.Help.Ansi.AUTO.string(msg));
	}

	private void sendSMS(String to, String from, String wholeMessage) {
		Twilio.init(
			System.getenv("TWILIO_ACCOUNT_SID"),
			System.getenv("TWILIO_AUTH_TOKEN"));

		Message
			.creator(new PhoneNumber(to), new PhoneNumber(from), wholeMessage)
			.create();
	}
}
