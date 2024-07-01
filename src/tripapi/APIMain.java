package tripapi;

import tripapi.service.TripAPIService;

public class APIMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// 1. TripAPIService 생성 및 메서드 호출 (Service 내에서 요청 파라미터 검증, 반환값 검증, 에러 처리 필요)
		TripAPIService tripAPIService = new TripAPIService();
		//tripAPIService.provideServiceLocalCode();

		// 2. tripAPIService 객체의 지역기반관광정보조회
		//tripAPIService.provideServiceTravelDestination();
		
		// 3. 공통 정보 조회
		tripAPIService.provideServiceTravelDetail();
		
		// 4. 무장애 정보 조회
		tripAPIService.provideServiceDisabilityAccommodationInfo();
	}
}
