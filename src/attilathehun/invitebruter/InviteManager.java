package attilathehun.invitebruter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class InviteManager implements Serializable {
    private Bruter bruter;

    public InviteManager(Bruter b) {
        this.bruter = b;
    }

    /**
     * Generates invite link for given SourceArrayVault
     * @return invite link based on current attempt base
     */
    public static String generateLink(SourceArrayVault position) {
        StringBuilder builder = new StringBuilder();
        for(int number : position.array()) {
            builder.append(Bruter.getCharset().charAt(number));
        }
        return Bruter.getBaseURL().concat(new String(builder));
    }

    /**
     * Gathers information about given invite link and returns them as a custom Invite object
     * @param inviteLink the url to resolve
     * @return Invite object with the information found
     * @throws IOException MalformedURLException, ProtocolException
     */
    public static Invite resolve(String inviteLink) throws IOException {
            HttpURLConnection connection = (HttpURLConnection) new URL(inviteLink).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            InputStream response = connection.getInputStream();
            String input =  new String(response.readAllBytes());
            response.close();
            String title = input.substring(input.indexOf("<title>") + "<title>".length(), input.indexOf("</title>"));
            Invite invite = new Invite(inviteLink);
            invite.setResponseCode(connection.getResponseCode());

            if(!title.equals("Discord")) {
                invite.setValid();
                invite.setName(title);
            }

            return invite;
    }

    public static class Invite {
        private static final int[] availableLengths = {7, 8, 10};

        private boolean isValid = false;
        private String link;
        private String code;
        private String name = "Discord";
        private int responseCode;

        private Invite() {

        }

        private Invite(String link) {
            this.link = link;
            this.code = link.substring(link.indexOf("/"));
        }

        private void setValid() {
            this.isValid = true;
        }

        public boolean isValid() {
            return isValid;
        }

        public String link() {
            return link;
        }

        public String code() {
            return code;
        }

        public void setResponseCode(int responseCode) {
            this.responseCode = responseCode;
        }

        public int responseCode() {
            return responseCode;
        }

        public static int[] getAvailableLengths() {
            return availableLengths;
        }

        public String name() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }
    }
}
