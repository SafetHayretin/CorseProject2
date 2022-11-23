package Html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ConnectionHtml {
    private String link;

    private static final String FILES_DIRECTORIES = "C:\\Users\\Safet\\Desktop\\CorseProject2\\Html\\";

    private static final String IMAGE_DIRECTORIES = "C:\\Users\\Safet\\Desktop\\CorseProject2\\Images\\";

    private final List<String> externalLinks = new ArrayList<>();

    public ConnectionHtml(String link) {
        this.link = link;
    }

    public void run() {
        try {
            Document doc = Jsoup.connect(link).get();
            Element element = doc.body();
            Elements elements = doc.getElementsByTag("img");
            Elements externalLinks = doc.getElementsByTag("a");
            writeHtmlToFile(element, elements);
            getExternalLinks(element, externalLinks);
            walkExternalLink();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void walkExternalLink() {
        for (String link : externalLinks) {
            System.out.println("Currunt link: " + link);
            try {
                Document doc = Jsoup.connect(link).get();
                Element element = doc.body();
                Elements elements = doc.getElementsByTag("img");
                writeHtmlToFile(element, elements);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writeHtmlToFile(Element element, Elements elements) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(FILES_DIRECTORIES);
        String id = element.className();
        sb.append(id).append(".txt");
        File file = new File(sb.toString());
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (Element e : elements) {
            String link = e.attr("src");
            writer.write(link + "\n");
            downloadPhotos(link);
        }
        writer.close();
    }

    private void getExternalLinks(Element element, Elements elements) {
        for (Element e : elements) {
            String link = e.attr("href");
            if (link.startsWith("http"))
                externalLinks.add(link + "\n");
        }
        System.out.println(externalLinks);
        System.out.println(externalLinks.size());
    }

    private void downloadPhotos(String link) throws MalformedURLException {
        URL url = new URL(link);
        try (InputStream in = new BufferedInputStream(url.openStream());
             ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            byte[] buf = new byte[1024];
            for (int n; -1 != (n = in.read(buf)); ) {
                out.write(buf, 0, n);
            }
            byte[] response = out.toByteArray();
            String name = getName(link);
            FileOutputStream fos = new FileOutputStream(IMAGE_DIRECTORIES + name);
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
}
