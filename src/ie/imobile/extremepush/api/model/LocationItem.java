package ie.imobile.extremepush.api.model;

public final class LocationItem {
    public String id;
    public String title;
    public double latitude;
    public double longitude;
    public float radius;

    @Override
    public String toString() {
    	return "id: " + id + " title: " + title
    			+ " latitude: " + latitude + " longitude " + longitude
    			+ " radius: " + radius;
    }
    
}
