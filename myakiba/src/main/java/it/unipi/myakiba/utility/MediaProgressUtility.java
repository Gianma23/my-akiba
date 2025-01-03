package it.unipi.myakiba.utility;

import it.unipi.myakiba.enumerator.MediaProgress;
import it.unipi.myakiba.enumerator.MediaStatus;
import lombok.Data;

@Data
public class MediaProgressUtility {
    private int total;                  // total number of chapters/episodes
    private int current;                // current chapters/episodes read/watched
    private MediaStatus status;         // status of the media (ongoing/completed)

    public MediaProgress getProgress() {
        MediaProgress progress;
        if (this.current == 0)
            progress = MediaProgress.PLANNED;
        else if ((this.current == this.total) && (this.status == MediaStatus.COMPLETE))
            progress = MediaProgress.COMPLETED;
        else
            progress = MediaProgress.IN_PROGRESS;

        return progress;
    }
}
