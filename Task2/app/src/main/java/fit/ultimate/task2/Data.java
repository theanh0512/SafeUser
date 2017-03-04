package fit.ultimate.task2;

/**
 * Created by Pham on 5/3/2017.
 */

public class Data {
    private String uri;
    private String contactName;

    public Data(String uri, String contactName) {
        this.contactName = contactName;
        this.uri = uri;
    }

    public String getUri() {
        return this.uri;
    }

    public String getContactName() {
        return contactName;
    }
}
