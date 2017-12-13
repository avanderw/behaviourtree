package net.avdw.behaviourtree;

import java.util.ArrayList;
import java.util.List;
import org.pmw.tinylog.Logger;

public abstract class ABehaviourTree {

    List<ABehaviourTree> children = new ArrayList();

    public ABehaviourTree(ABehaviourTree... children) {
        add(children);
    }

    public final void add(ABehaviourTree... children) {
        for (ABehaviourTree child : children) {
            if (child == null) {
                throw new NullPointerException("child cannot be null");
            }

            this.children.add(child);
        }
    }

    public abstract Status process();

    /**
     * Fallback nodes are used to find and execute the first child that does not
     * fail. A fallback node will return immediately with a status code of
     * success or running when one of its children returns success or running.
     * The children are ticked in order of importance, from left to right.
     *
     * @author Andrew van der Westhuizen
     */
    public static class Selector extends ABehaviourTree {

        public Selector() {
        }

        public Selector(ABehaviourTree... children) {
            super(children);
        }

        @Override
        public Status process() {
            Logger.debug(this);
            for (ABehaviourTree child : children) {
                Status status = child.process();
                switch (status) {
                    case Running:
                    case Success:
                        return status;
                }
            }

            return Status.Failure;
        }
    }

    /**
     * Sequence nodes are used to find and execute the first child that has not
     * yet succeeded. A sequence node will return immediately with a status code
     * of failure or running when one of its children returns failure or
     * running. The children are ticked in order, from left to right.
     *
     * @author Andrew van der Westhuizen
     */
    public static class Sequence extends ABehaviourTree {

        public Sequence() {
        }

        public Sequence(ABehaviourTree... children) {
            super(children);
        }

        @Override
        public Status process() {
            Logger.debug(this);
            for (ABehaviourTree child : children) {
                Status status = child.process();
                switch (status) {
                    case Running:
                    case Failure:
                        return status;
                }
            }

            return Status.Success;
        }
    }

    /**
     * Will repeat the wrapped task until that task fails.
     *
     * @author Andrew van der Westhuizen
     */
    public static class UntilFail extends ABehaviourTree {

        public UntilFail() {
        }

        public UntilFail(ABehaviourTree child) {
            super(child);
        }

        @Override
        public Status process() {
            if (children.size() != 1) {
                throw new AssertionError(children);
            }

            Status status = children.get(0).process();
            switch (status) {
                case Running:
                case Success:
                    return Status.Running;
                case Failure:
                    return Status.Success;
                default:
                    throw new AssertionError(status.name());
            }
        }
    }

    public enum Status {
        Running, Failure, Success
    }
}
