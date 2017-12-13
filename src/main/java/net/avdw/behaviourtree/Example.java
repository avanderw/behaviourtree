/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.avdw.behaviourtree;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;

public class Example {

    public static void main(String[] args) {
        Configurator.currentConfig()
                .formatPattern("{date:yyyy-MM-dd HH:mm:ss} [{thread}] {class}.{method}() {level}: {message}")
                .level(Level.TRACE)
                .activate();
        
        ABehaviourTree walkToDoor = new ABehaviourTree() {
            @Override
            public ABehaviourTree.Status process() {
                Logger.debug("walkToDoor");
                return ABehaviourTree.Status.Success;
            }
        };
        ABehaviourTree openDoor = new ABehaviourTree() {
            @Override
            public ABehaviourTree.Status process() {
                Logger.debug("openDoor");
                return ABehaviourTree.Status.Failure;
            }
        };
        ABehaviourTree unlockDoor = new ABehaviourTree() {
            @Override
            public ABehaviourTree.Status process() {
                Logger.debug("unlockDoor");
                return ABehaviourTree.Status.Success;
            }
        };
        ABehaviourTree smashDoor = new ABehaviourTree() {
            @Override
            public ABehaviourTree.Status process() {
                Logger.debug("smashDoor");
                return ABehaviourTree.Status.Success;
            }
        };
        ABehaviourTree walkThroughDoor = new ABehaviourTree() {
            @Override
            public ABehaviourTree.Status process() {
                Logger.debug("walkThroughDoor");
                return ABehaviourTree.Status.Success;
            }
        };
        ABehaviourTree closeDoor = new ABehaviourTree() {
            @Override
            public ABehaviourTree.Status process() {
                Logger.debug("closeDoor");
                return ABehaviourTree.Status.Success;
            }
        };

        ABehaviourTree behaviourTree = new ABehaviourTree.Sequence();
        behaviourTree.add(walkToDoor, new ABehaviourTree.Selector(openDoor, new ABehaviourTree.Sequence(unlockDoor, openDoor), smashDoor), walkThroughDoor, closeDoor);

        behaviourTree.process();
    }

}
