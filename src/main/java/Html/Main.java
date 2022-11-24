package Html;

import org.apache.commons.cli.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors());
        Options options = new Options();

        Option outputDirectory = new Option("output_dir", true, "Output directory to save photos");
        outputDirectory.setRequired(true);
        options.addOption(outputDirectory);

        Option imageFormat = new Option("image_format", true, "Image format to be saved");
        options.addOption(imageFormat);

        Option userAgent = new Option("user_agent", true, "User agent to set");
        options.addOption(userAgent);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        ConnectionHtml con = new ConnectionHtml("https://www.ozone.bg/", cmd);
        con.run();

        int coreCount = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(coreCount);

        List<String> externalLinks = con.getExternalLinks();
        for (String link : externalLinks) {
            service.execute(new ConnectionHtml(link, cmd));
        }
    }
}
