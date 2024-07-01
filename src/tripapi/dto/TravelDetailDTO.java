package tripapi.dto;

public class TravelDetailDTO {

	int contentid;
	String homepage;
	String overview;
	
	public TravelDetailDTO() {};
	
	public TravelDetailDTO(int contentid, String homepage, String overview) {
		this.contentid = contentid;
		this.homepage = homepage;
		this.overview = overview;
	}

	public int getContentid() {
		return contentid;
	}

	public void setContentid(int contentid) {
		this.contentid = contentid;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}
}
