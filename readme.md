# simple behaviour tree

This is a one class example that implements basic functionality for a behaviour tree. Currently it supports sequence, selector, until-fail nodes.

## usage

    ABehaviourTree walkToDoor = new ABehaviourTree() {
        @Override
        public ABehaviourTree.Status process() {
            Logger.debug("walkToDoor");
            return ABehaviourTree.Status.Success;
        }
    };

    ABehaviourTree behaviourTree = new ABehaviourTree.Selector();

    behaviourTree.add(walkToDoor);