package Html;

import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {
//        Options options = new Options();
//
//        Option outputDirectory = new Option("output_dir", true, "Output directory to save photos");
//        outputDirectory.setRequired(true);
//        options.addOption(outputDirectory);
//
//        Option imageFormat = new Option("image_format", true, "Image format to be saved");
//        options.addOption(imageFormat);
//
//        CommandLineParser parser = new DefaultParser();
//        HelpFormatter formatter = new HelpFormatter();
//        CommandLine cmd = null;
//        try {
//            cmd = parser.parse(options, args);
//        } catch (ParseException e) {
//            System.out.println(e.getMessage());
//            formatter.printHelp("utility-name", options);
//            System.exit(1);
//        }
//
//        String format = cmd.getOptionValue("image-format");
//        String outputDirectoryImg = cmd.getOptionValue("output-dir");

        ConnectionHtml con = new ConnectionHtml("https://www.facebook.com/");
        con.run();
    }
}
