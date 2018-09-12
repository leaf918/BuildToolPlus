
package ai.keye.playground.j4make;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class Graph {
    private static long ID_GENERATOR = 1L;
//    private static final Logger LOG = LoggerFactory.getLogger(Graph.class);
    private static final String ROOT_NAME = "<ROOT>";
    private Map<String, Target> name2target = new java.util.HashMap<String, Target>();

    private Graph() {
        this.name2target.put(ROOT_NAME, new TargetImpl(ROOT_NAME));
    }

    public Collection<Target> getTargets() {
        return Collections.unmodifiableCollection(this.name2target.values());
    }

    public Target getRoot() {
        return getTargetByName(ROOT_NAME, false);
    }

    public Target getTargetByName(final String name) {
        return this.getTargetByName(name, false);
    }

    private Target getTargetByName(final String name, final boolean allowCreate) {
        Target t = this.name2target.get(name);
        if (t == null && allowCreate) {
            LOG.debug("creating node " + name);
            t = new TargetImpl(name);
            this.name2target.put(name, t);
        }
        return t;
    }

    private static String targetName(final String line) {
        int b = line.indexOf('`');
        if (b == -1) b = line.indexOf('\'');//GNU make 4.0
        int e = (b == -1 ? -1 : line.indexOf('\'', b + 1));
        if (b == -1 || e == -1 || b > e) {
            throw new IllegalArgumentException(
                    "Cannot get target name in \"" + line + "\".\n");
        }
        return line.substring(b + 1, e);
    }

    public static Graph parse(final BufferedReader in) throws IOException {
        final Graph g = new Graph();
        g.scan(ROOT_NAME, in);
        return g;
    }

    private void scan(
            final String rootName,
            final BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("Considering target file")) {
                final String childName = targetName(line);
                final Target child = this.getTargetByName(childName, true);
                final Target root = this.getTargetByName(rootName, true);
                TargetImpl.class.cast(root).addPrerequisite(child);
                this.scan(childName, in);
            } else if (line.startsWith("make") && line.contains(": Entering directory")) {
                //ignore
            } else if (line.startsWith("make: Leaving directory")) {
                //ignore
            } else if (line.startsWith("Must remake target ")) {
                final String tName = targetName(line);
                final Target t = this.getTargetByName(tName, true);
                while ((line = in.readLine()) != null) {
                    if (line.trim().startsWith("Successfully remade target file")) break;
                    else if (line.startsWith("make") && line.contains(": Entering directory")) {
                        continue;
                    } else if (line.startsWith("make: Leaving directory")) {
                        continue;
                    } else if (line.startsWith("Invoking recipe from")) {
                        continue;
                    }
                    t.shellLines.add(line);
                }
            } else if (line.startsWith("Pruning file ")) {
                final String tName = targetName(line);
                final Target dep = this.getTargetByName(tName, true);
                final Target root = this.getTargetByName(rootName, true);
                TargetImpl.class.cast(root).addPrerequisite(dep);
            } else if (line.startsWith("Finished prerequisites of target file ")) {
                final String tName = targetName(line);
                if (!tName.equals(rootName)) {
                    throw new IllegalStateException("expected " + rootName + " got " + line);
                }
                break;
            }
        }
    }

    private class TargetImpl extends Target {
        private long _id = ID_GENERATOR++;

        TargetImpl(final String name) {
            super(name);
        }

        @Override
        public long getNodeId() {
            return this._id;
        }

        @Override
        public Graph getGraph() {
            return Graph.this;
        }

        void addPrerequisite(final Target t) {
            if (t == this || t.getGraph() != this.getGraph()) throw new IllegalStateException();
            super._prerequisites.add(t);
        }
    }

}
