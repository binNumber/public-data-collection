package tripapi.dto;

public class TravelDestinationDTO {

	String addr1; // 주소
	String addr2; // 상세주소
	int areaCode; // 지역코드
	int sigunguCode; // 시군구 코드
	int contentId; // 콘텐츠ID
	int contentTypeId; // 콘텐츠타입
	String title; // 여행지 이름
	String firstImage; // 이미지1
	String secondImage; // 이미지2
	String mapX; // 맵X축
	String mapY; // 맵Y축
	int mLevel; // 맵레벨
	String tel; // 전화번호

	public TravelDestinationDTO() {

	}

	public TravelDestinationDTO(String addr1, String addr2, int areaCode, int sigunguCode, int contentId,
			int contentTypeId, String title, String firstImage, String secondImage, String mapX, String mapY,
			int mLevel, String tel) {
		this.addr1 = addr1;
		this.addr2 = addr2;
		this.areaCode = areaCode;
		this.sigunguCode = sigunguCode;
		this.contentId = contentId;
		this.contentTypeId = contentTypeId;
		this.title = title;
		this.firstImage = firstImage;
		this.secondImage = secondImage;
		this.mapX = mapX;
		this.mapY = mapY;
		this.mLevel = mLevel;
		this.tel = tel;
	}

	public String getAddr1() {
		return addr1;
	}

	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}

	public String getAddr2() {
		return addr2;
	}

	public void setAddr2(String addr2) {
		this.addr2 = addr2;
	}

	public int getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(int areaCode) {
		this.areaCode = areaCode;
	}

	public int getSigunguCode() {
		return sigunguCode;
	}

	public void setSigunguCode(int sigunguCode) {
		this.sigunguCode = sigunguCode;
	}

	public int getContentId() {
		return contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	public int getContentTypeId() {
		return contentTypeId;
	}

	public void setContentTypeId(int contentTypeId) {
		this.contentTypeId = contentTypeId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstImage() {
		return firstImage;
	}

	public void setFirstImage(String firstImage) {
		this.firstImage = firstImage;
	}

	public String getSecondImage() {
		return secondImage;
	}

	public void setSecondImage(String secondImage) {
		this.secondImage = secondImage;
	}

	public String getMapX() {
		return mapX;
	}

	public void setMapX(String mapX) {
		this.mapX = mapX;
	}

	public String getMapY() {
		return mapY;
	}

	public void setMapY(String mapY) {
		this.mapY = mapY;
	}

	public int getmLevel() {
		return mLevel;
	}

	public void setmLevel(int mLevel) {
		this.mLevel = mLevel;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
	
}
