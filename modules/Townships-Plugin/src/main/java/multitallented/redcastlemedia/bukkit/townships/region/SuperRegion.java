package multitallented.redcastlemedia.bukkit.townships.region;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;

/**
 *
 * @author Multitallented
 */
public class SuperRegion {
    private String name;
    private Location l;
    private String type;
    private final Map<String, List<String>> members;
    private final List<String> owners;
    private int power;
    private double taxes = 0;
    private double balance = 0;
    private LinkedList<Double> taxRevenue;
    private int maxPower;
    private ArrayList<Location> childLocations;
    private long lastDisable;
    private Map<String, Long> lastWarTick;
    
    public SuperRegion(String name, Location l, String type, List<String> owner, Map<String, List<String>> members,
            int power, double taxes, double balance, LinkedList<Double> taxRevenue, int maxPower, ArrayList<Location> childLocations,
            long lastDisable, Map<String, Long> lastWarTick) {
        this.name = name;
        this.l = l;
        this.type=type;
        this.owners = owner;
        this.members = members;
        this.power = power;
        this.taxes = taxes;
        this.balance = balance;
        this.taxRevenue = taxRevenue;
        this.maxPower = maxPower;
        this.childLocations=childLocations;
        this.lastDisable=lastDisable;
        this.lastWarTick=lastWarTick;
    }

    public void setLastWarTick(Map<String, Long> newLastWarTick) {
        this.lastWarTick = newLastWarTick;
    }

    public Map<String, Long> getLastWarTick() { return lastWarTick; }

    protected long getLastDisable() {
        return lastDisable;
    }

    protected void setLastDisable(long lastDisable) {
        this.lastDisable = lastDisable;
    }
    
    protected void setLocation(Location l) {
        this.l = l;
    }
    
    public ArrayList<Location> getChildLocations() {
        return this.childLocations;
    }
    
    protected void setType(String type) {
        this.type = type;
    }
    
    protected void setMaxPower(int maxPower) {
        this.maxPower = maxPower;
    }
    
    public int getMaxPower() {
        return maxPower;
    }
    
    public LinkedList<Double> getTaxRevenue() {
        return taxRevenue;
    }
    
    public void addTaxRevenue(double input) {
        taxRevenue.addFirst(input);
        if (taxRevenue.size() > 5) {
            taxRevenue.removeLast();
        }
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public double getTaxes() {
        return taxes;
    }
    
    public void setTaxes(double taxes) {
        this.taxes = taxes;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Location getLocation() {
        return l;
    }
    
    public String getType() {
        return type;
    }
    
    public boolean hasMember(String name) {
        return members.containsKey(name);
    }
    
    public boolean addMember(String name, List<String> perms) {
        return members.put(name, perms) != null;
    }
    
    public List<String> getMember(String name) {
        return members.get(name);
    }
    
    public Map<String, List<String>> getMembers() {
        return members;
    }
    
    public boolean togglePerm(String name, String perm) {
        boolean removed = false;
        try {
            if (!members.get(name).remove(perm))
                members.get(name).add(perm);
            else
                removed = true;
        } catch (NullPointerException npe) {
            
        }
        return removed;
    }
    
    public boolean hasOwner(String name) {
        return owners.contains(name);
    }
    
    public boolean addOwner(String name) {
        return owners.add(name);
    }
    
    public List<String> getOwners() {
        return owners;
    }
    
    public boolean remove(String name) {
        if (!owners.remove(name))
            return members.remove(name) != null;
        else
            return true;
    }
    
    public int getPower() {
        return power;
    }
    
    public void setPower(int i) {
        power = i;
    }
    
    public int getPopulation() {
        int membersSize = 0;
        for (String s : members.keySet()) {
            if (members.get(s).contains("member")) {
                membersSize += 1;
            }
        }
        return owners.size() + membersSize;
    }
}
