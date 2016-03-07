package org.calorycounter.shared.models;

import org.calorycounter.shared.models.JSONSerializable;
import org.json.simple.JSONObject;
import static org.calorycounter.shared.Constants.network.*;


public class Sport implements JSONSerializable{
	
	private Long id;
	private String name;
	private Integer duration;
	private Float energyConsumed;


	public Sport() {
		this.id = null;
		this.name = null;
		this.duration = null;
		this.energyConsumed = null;
	}

	public Sport(Long id, String name, Integer duration, Float energyConsumed){
		this.id = id;
		this.name = name;
		this.duration = duration;
		this.energyConsumed = energyConsumed;
	}

	public Sport(String name, Integer duration, Float energyConsumed){
		this.name = name;
		this.duration = duration;
		this.energyConsumed = energyConsumed;
	}

	public Long getId(){
		return id;
	}

	public void setId(Long id){
		this.id= id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDuration(){
		return duration;
	}

	public void setDuration(int duration){
		this.duration = duration;
	}

	public Float getEnergyConsumed(){
		return energyConsumed;
	}

	public void setEnergyConsumed(float energyConsumed){
		this.energyConsumed= energyConsumed;
	}

	@Override
	public JSONObject toJSON(){
		JSONObject repr = new JSONObject();
		repr.put(SPORT_NAME, this.name);
		repr.put(SPORT_DURATION, Integer.toString(this.duration));
		repr.put(SPORT_ENERGY_CONSUMED, Float.toString(this.energyConsumed));
		return repr;
	}

	@Override
	public void initFromJSON(JSONObject obj){
		this.setName((String) obj.get(SPORT_NAME));
		this.setDuration(Integer.parseInt((String) obj.get(SPORT_DURATION)));
		this.setEnergyConsumed(Float.parseFloat((String) obj.get(SPORT_ENERGY_CONSUMED)));
	}
}