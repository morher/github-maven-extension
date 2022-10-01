package no.nav.bidrag.maven.github;

import java.util.ArrayList;
import java.util.List;

public class GroupLevel {
    private final GroupLevel parentLevel;
    private final List<GroupLevel> sublevels = new ArrayList<>();
    private boolean started;

    public GroupLevel() {
        this.parentLevel = null;
    }

    private GroupLevel(GroupLevel parentLevel) {
        this.parentLevel = parentLevel;
        this.parentLevel.sublevels.add(this);
    }

    public void start(String title) {
        if (parentLevel != null && !parentLevel.started) {
            throw new IllegalStateException("Parent level not started");
        }
        end();
        System.out.println("::group::" + title);
        started = true;
    }

    public void end() {
        if (started) {
            for (GroupLevel sublevel : sublevels) {
                sublevel.end();
            }
            System.out.println("::endgroup::");
            started = false;
        }
    }

    public GroupLevel sublevel() {
        return new GroupLevel(this);
    }
}
