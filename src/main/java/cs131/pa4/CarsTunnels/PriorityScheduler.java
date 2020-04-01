package cs131.pa4.CarsTunnels;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cs131.pa4.Abstract.Scheduler;
import cs131.pa4.Abstract.Tunnel;
import cs131.pa4.Abstract.Vehicle;

/**
 * The priority scheduler assigns vehicles to tunnels based on their priority
 * It extends the Scheduler class.
 * @author Yuchen Xu
 *
 */
public class PriorityScheduler extends Scheduler{

	/**
	 * Creates an instance of a priority scheduler with the given name and tunnels
	 * @param name the name of the priority scheduler
	 * @param tunnels the tunnels where the vehicles will be scheduled to
	 */
	private Comparator<Vehicle> vpriority = new Comparator<Vehicle>() {
		@Override
		public int compare(Vehicle v1, Vehicle v2) {
			return v1.getPriority() - v2.getPriority();
		}
	};
	private PriorityQueue<Vehicle> priorityqueue;
    private Lock lock;
    private Collection<Tunnel> tunnels;
    private String name;
    private Condition emptyTunnelBased;
    private Condition priorityBased;
    private Map<Vehicle, BasicTunnel> carMap;
	 /**
	 *
	 * @param name
	 * @param tunnels
	 */
	public PriorityScheduler(String name, Collection<Tunnel> tunnels) {
		super(name, tunnels);
		this.lock = new ReentrantLock();
		this.tunnels = tunnels;
		this.emptyTunnelBased = lock.newCondition();
		this.priorityBased = lock.newCondition();
        this.carMap = new HashMap<>();
		this.priorityqueue = new PriorityQueue<>(this.vpriority);
	}

	/**
	 * admit the vehicle to enter
	 * @param vehicle the vehicle to admit.
	 * @return tunnel that the vehicle is in
	 */
	@Override
	public Tunnel admit(Vehicle vehicle) {
		lock.lock();
		try{
			BasicTunnel basicTunnel = null;
			priorityqueue.offer(vehicle);//add the vehicle to the waiting queue
			//lock if the vehicle is not of the top priority
			while(vehicle != priorityqueue.peek()){
				try{
					priorityBased.await();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			//lock if the avaliable tunnel is null
			while(basicTunnel == null){
				basicTunnel = enterTunnel(vehicle);
				if (basicTunnel == null){

					emptyTunnelBased.await();
				}

			}
			priorityqueue.remove(vehicle);
			priorityBased.signalAll();
		}catch(Exception e){
			e.printStackTrace();
		}

		lock.unlock();
		return carMap.get(vehicle);
	}

	/**
	 * check if the car can enter the tunnel, return the target tunnel
	 * @param vehicle
	 * @return the tunnel that the vehicle entered into
	 */
	private BasicTunnel enterTunnel(Vehicle vehicle){
		if(tunnels == null){

			return null;
		}

		for(Tunnel t: tunnels){
			BasicTunnel basicTunnel = (BasicTunnel) t;
			if(basicTunnel.tryToEnterInner(vehicle)){
				carMap.put(vehicle,basicTunnel);
				return basicTunnel;
			}
		}
		return null;
	}


	/**
	 * let the car exit the tunnel, signal all the waiting thread if a vehicle has exit
	 * @param vehicle the vehicle to exit from its tunnel
	 */
	@Override
	public void exit(Vehicle vehicle) {
		lock.lock();
		try{
			BasicTunnel nowIn = carMap.get(vehicle);
			nowIn.exitTunnelInner(vehicle);
			carMap.remove(vehicle);
			emptyTunnelBased.signalAll();
		}finally {
			lock.unlock();
		}
	}
	
}
