package no.nav.bidrag.maven.github;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.eventspy.EventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

@Component(role = EventSpy.class, hint = "spy")
public class GithubActionsLogGrouper extends AbstractEventSpy {

    @Requirement
    private Logger logger;

    private final GroupLevel projectGroup = new GroupLevel();
    private final GroupLevel mojoGroup = projectGroup.sublevel();
    private final boolean enabled = "true".equals(System.getenv("GITHUB_ACTIONS"));

    @Override
    public void onEvent(Object event) throws Exception {
        if (event instanceof ExecutionEvent) {
            onExecutionEvent((ExecutionEvent) event);
        }
    }

    private void onExecutionEvent(ExecutionEvent event) {
        if (!enabled) {
            return;
        }

        switch (event.getType()) {
        case ProjectStarted:
            MavenProject project = event.getProject();
            projectGroup.start("Building " + project.getName() + project.getVersion());
            break;

        case ProjectSucceeded:
        case ProjectSkipped:
        case ProjectFailed:
            projectGroup.end();
            break;

        case MojoStarted:
            MojoExecution mojo = event.getMojoExecution();
            mojoGroup.start(mojo.getArtifactId() + ":" + mojo.getVersion() + ":" + mojo.getGoal());
            break;

        case MojoSucceeded:
        case MojoSkipped:
        case MojoFailed:
            mojoGroup.end();
            break;

        default:
            break;
        }
    }
}
