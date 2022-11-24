package Html;

import org.apache.commons.cli.CommandLine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ConnectionHtml implements Runnable {
    private String link;

    private static String imagesDirectory = "C:\\Users\\Safet\\Desktop\\CorseProject2\\Images\\";

    private final List<String> externalLinks = new ArrayList<>();

    private static final List<String> checkedLinks = new ArrayList<>();

    private static final List<String> downloadedPhotos = new ArrayList<>();

    private final HttpClient client;

    private final CommandLine cmd;

    public ConnectionHtml(String link, CommandLine cmd) {
        this.cmd = cmd;
        this.link = link;
        this.client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        //imagesDirectory = cmd.getOptionValue("output_dir");
    }

    @Override
    public void run() {
        try {
            System.out.println("Current link: " + link);
            HttpRequest request = createConnectionRequest();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Document doc = Jsoup.parse(response.body(), response.uri().toString());
            Elements elements = doc.getElementsByTag("img");
            downloadPhotosFromHtml(elements);

            Elements externalLinksElement = doc.getElementsByTag("a");
            saveExternalLinks(externalLinksElement);

            checkedLinks.add(link);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpRequest createConnectionRequest() throws URISyntaxException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(new URI(link)).GET();
        if (cmd.hasOption("user_agent"))
            builder.header("User-Agent", cmd.getOptionValue("user_agent"));

        return builder.build();
    }

    private void downloadPhotosFromHtml(Elements elements) throws IOException {
        for (Element e : elements) {
            String link = e.attr("src");
            if (downloadedPhotos.contains(link))
                continue;
            downloadPhotos(link);
            downloadedPhotos.add(link);
        }
    }

    private void saveExternalLinks(Elements elements) {
        for (Element e : elements) {
            String link = e.attr("href");
            if (link.startsWith("http"))
                externalLinks.add(link);
        }
        System.out.println(externalLinks.size());
    }

    private void downloadPhotos(String link) throws MalformedURLException {
        URL url = new URL(link);
        System.out.println(link);
        try (InputStream in = new BufferedInputStream(url.openStream());
             ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            byte[] buf = new byte[1024];
            for (int n; -1 != (n = in.read(buf)); ) {
                out.write(buf, 0, n);
            }
            byte[] response = out.toByteArray();
            String name = getName(link);
            FileOutputStream fos = new FileOutputStream(imagesDirectory + name);
            fos.write(response);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getName(String link) {
        String[] splitted = link.split("/");
        int lastIndex = splitted.length - 1;
        String name = splitted[lastIndex];
        splitted = name.split("/?");
        name = splitted[0];
        return name;
    }

    public List<String> getExternalLinks() {
        return this.externalLinks;
    }
}
