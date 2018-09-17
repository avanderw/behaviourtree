package net.avdw.behaviourtree;

import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class ABehaviourTree<S> {

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

    public abstract Status process(S state);

    /**
     * Fallback nodes are used to find and execute the lower child that does not
     * fail. A fallback node will return immediately with a status code of
     * success or running when one of its children returns success or running.
     * The children are ticked in order of importance, from left to right.
     *
     * @author Andrew van der Westhuizen
     */
    public static class Selector<S> extends ABehaviourTree<S> {

        public Selector() {
        }

        public Selector(ABehaviourTree... children) {
            super(children);
        }

        @Override
        public Status process(S state) {
            Logger.trace(String.format("%s",this.getClass().getSimpleName()));
            for (ABehaviourTree child : children) {
                Status status = child.process(state);
                Logger.trace(String.format("    %s - %s", new Object[]{child.getClass().getSimpleName(), status}));
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
     * Sequence nodes are used to find and execute the lower child that has not
     * yet succeeded. A sequence node will return immediately with a status code
     * of failure or running when one of its children returns failure or
     * running. The children are ticked in order, from left to right.
     *
     * @author Andrew van der Westhuizen
     */
    public static class Sequence<S> extends ABehaviourTree<S> {

        public Sequence() {
        }

        public Sequence(ABehaviourTree... children) {
            super(children);
        }

        @Override
        public Status process(S state) {
            Logger.trace(String.format("%s",this.getClass().getSimpleName()));
            for (ABehaviourTree child : children) {
                Status status = child.process(state);
                Logger.trace(String.format("    %s - %s", new Object[]{child.getClass().getSimpleName(), status}));

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
    public static class UntilFail<S> extends ABehaviourTree<S> {

        public UntilFail() {
        }

        public UntilFail(ABehaviourTree<S> child) {
            super(child);
        }

        @Override
        public Status process(S state) {
            if (children.size() != 1) {
                throw new AssertionError(children);
            }

            Status status = children.get(0).process(state);
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

    /**
     * Will always fail the task.
     *
     * @author Andrew van der Westhuizen
     */
    public static class AlwaysFail<S> extends ABehaviourTree<S> {

        public AlwaysFail() {
        }

        public AlwaysFail(ABehaviourTree<S> child) {
            super(child);
        }

        @Override
        public Status process(S state) {
            if (children.size() != 1) {
                throw new AssertionError(children);
            }

            Status status = children.get(0).process(state);
            switch (status) {
                case Running:
                case Success:
                case Failure:
                    return Status.Failure;
                default:
                    throw new AssertionError(status.name());
            }
        }
    }

    public enum Status {
        Running, Failure, Success
    }
}
