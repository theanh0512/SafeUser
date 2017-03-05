package fit.ultimate.task1;

/**
 * Created by Pham on 5/3/2017.
 */

public class Task {
    private String caption;
    private int entityId;

    public Task(String caption, int entityId) {
        this.caption = caption;
        this.entityId = entityId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
}
