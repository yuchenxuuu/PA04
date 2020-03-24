package cs131.pa4.CarsTunnels;

import java.util.Collection;

import cs131.pa4.Abstract.Direction;
import cs131.pa4.Abstract.Factory;
import cs131.pa4.Abstract.Scheduler;
import cs131.pa4.Abstract.Tunnel;
import cs131.pa4.Abstract.Vehicle;

/**
 * The class implementing the Factory interface for creating instances of classes
 * @author cs131a
 *
 */
public class ConcreteFactory implements Factory {

    @Override
    public Tunnel createNewBasicTunnel(String name){
    		throw new UnsupportedOperationException("Not supported yet.");    
    }

    @Override
    public Vehicle createNewCar(String name, Direction direction){
    		throw new UnsupportedOperationException("Not supported yet.");    
    }

    @Override
    public Vehicle createNewSled(String name, Direction direction){
    		throw new UnsupportedOperationException("Not supported yet.");    
    }

    @Override
    public Scheduler createNewPriorityScheduler(String name, Collection<Tunnel> tunnels){
    		throw new UnsupportedOperationException("Not supported yet.");
    }
}
