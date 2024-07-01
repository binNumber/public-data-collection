package tripapi.service;

import java.util.ArrayList;
import java.util.HashMap;

import tripapi.dao.DisabilityAccommodationInfoDAO;
import tripapi.dao.LocalCodeDAO;
import tripapi.dao.TravelDestinationDAO;
import tripapi.dao.TravelDetailDAO;
import tripapi.dto.DisabilityAccommodationInfoDTO;
import tripapi.dto.LocalCodeDTO;
import tripapi.dto.TravelDestinationDTO;
import tripapi.dto.TravelDetailDTO;
import tripapi.repository.DisabilityAccommodationInfoRepository;
import tripapi.repository.LocalCodeRepository;
import tripapi.repository.TravelDestinationRepository;
import tripapi.repository.TravelDetailRepository;

public class TripAPIService {

	// 필드
	public String serviceKey;
	public int requestCount;
	
	LocalCodeRepository localCodeRepos;
	TravelDestinationRepository travelDestinationRepos;
	TravelDetailRepository travelDetailRepos;
	DisabilityAccommodationInfoRepository disabilityAccommodationInfoRepos;

	LocalCodeDAO localCodeDAO;
	TravelDestinationDAO travelDestinantionDAO;
	TravelDetailDAO travelDetailDAO;
	DisabilityAccommodationInfoDAO disabilityAccommodationInfoDAO;

	// 생성자
	public TripAPIService() {
		serviceKey = "your ServiceKey";
		requestCount = 1000;
		
		// Repository
		localCodeRepos = new LocalCodeRepository(serviceKey);
		travelDestinationRepos = new TravelDestinationRepository(serviceKey);
		travelDetailRepos = new TravelDetailRepository(serviceKey);
		disabilityAccommodationInfoRepos = new DisabilityAccommodationInfoRepository(serviceKey);

		// DAO
		travelDestinantionDAO = new TravelDestinationDAO();
		localCodeDAO = new LocalCodeDAO();
		travelDetailDAO = new TravelDetailDAO(requestCount);
		disabilityAccommodationInfoDAO = new DisabilityAccommodationInfoDAO(requestCount);
	}

	// 1. 전체 지역 코드 조회 (API) => JSON 파싱 => DB Merge
	public void provideServiceLocalCode() {
		int result = 0;
		ArrayList<LocalCodeDTO> localCodelist = null;

		// 1. 반복 횟수 결정 (API 에서 total Count 조회 후 반복 횟수 계산하는 함수)
		localCodeRepos.setLoopCount(1000); // 한 페이지애 출력 할 데이터 수 (API에 요청 예정인 numOfRows)

		// 2. 반복문 (API 조회 -> JSON 파싱 -> DB MERGE) 실행
		for (int currentPage = 0; currentPage < localCodeRepos.loopCount; currentPage++) {
			// 2.1) 전체 지역 코드 정보 조회 (API)
			String travelDestJSON = localCodeRepos.selectAllLocalCodeByAPI(currentPage);

			// 2.2) JSON 파싱 (현재 메모리에는 이전 리스트가 들어있는 상황)
			localCodelist = null; // 기존 list 참조 해제 -> GC에 의한 메모리 할당 해제 기대
			localCodelist = localCodeRepos.parseJson(travelDestJSON);

			// 2.3) 파싱 후 저장된 <LocalCodeDTO>List를 DAO에 접근 및 DB Merge
			result += localCodeDAO.saveLocalCode(localCodelist);

			System.out.println("사이클 " + (currentPage + 1) + "번 완료");
		}

		// 3) 결과 확인
		System.out.println("영향 받은 행 : " + result);

	}

	// 2. 전체 지역기반 관광정보 조회 (API) -> JSON 파싱 -> DB Merge
	public void provideServiceTravelDestination() {
		int result = 0;
		ArrayList<TravelDestinationDTO> travelDestinationList = null;

		// 1) 반복 횟수 결정 (API 에서 total Count 조회 후 반복 횟수 계산하는 함수)
		travelDestinationRepos.setLoopCount(1000); // 한 페이지애 출력 할 데이터 수 (API에 요청 예정인 numOfRows)

		// 2) 반복문 (API 조회 -> JSON 파싱 -> DB MERGE) 실행
		for (int currentPage = 0; currentPage < travelDestinationRepos.loopCount; currentPage++) { // i = 현재 페이지 번호
			// 2.1) 전체 지역기반 관광정보 조회 (API)
			String travelDestJSON = travelDestinationRepos.selectAllTravelDestinationByAPI(currentPage);

			// 2.2) JSON 파싱 (현재 메모리에는 이전 리스트가 들어있는 상황)
			travelDestinationList = null; // 기존 list 참조 해제 -> GC에 의한 메모리 할당 해제 기대
			travelDestinationList = travelDestinationRepos.parseJson(travelDestJSON);

			// 2.3) 파싱 후 저장된 <TravelDestinationDTO>List를 DAO에 접근 및 DB Merge
			result += travelDestinantionDAO.saveTravleDestination(travelDestinationList);

			System.out.println("사이클 " + (currentPage + 1) + "번 완료");
		}

		// 3) 결과 확인
		System.out.println("영향 받은 행 : " + result);

	}

	// 3. 공통 정보 조회 (API)
	// contentid 조회 (DB, TRAVEL_DESTINATION 테이블) -> 공통 정보 조회 (API) -> JSON 파싱 -> DB Merge
	public void provideServiceTravelDetail() {
		int mergeResult = 0;
		int updateResult = 0;
		
		ArrayList<TravelDetailDTO> travelDetailList = null;

		// 1) contentId 조회 및 저장 (DB - Travle_destination 테이블 Select)
		ArrayList<Integer> contentIDList = travelDetailDAO.getContentIdList();
		HashMap<Integer, String> jsonMap = new HashMap<Integer, String>();

		// 2) contentid로 공통 정보 조회 (API) 후 String 리스트에 저장
		for (int contentID : contentIDList) {
			String json = travelDetailRepos.selectTravelDetailByAPI(contentID);
			if(json != null) {
				jsonMap.put(contentID, json);
			}
		}

		System.out.println("api 조회 완료");

		// 3) jsonList의 JSON 파싱
		if(!jsonMap.isEmpty())
			travelDetailList = travelDetailRepos.parseJson(jsonMap);
		
		System.out.println("파싱 완료");

		// 4) DB Merge
		if(travelDetailList != null)
			mergeResult += travelDetailDAO.saveTravleDestination(travelDetailList);
		
		// 5) TB_REQUEST_CHECK Table Update
		if(!jsonMap.isEmpty())
			updateResult += travelDetailDAO.updateTableRequestCheck(jsonMap);
			
		// 6) 결과 확인
		System.out.println("Merge 수 : " + mergeResult);
		System.out.println("Update 수 : " + updateResult);
	}

	// 4. 무장애 정보 조회 (API)
	// contentid 조회 (DB, TRAVEL_DESTINATION 테이블) -> 소개정보조회 (API) -> JSON파싱 -> DB Merge
	public void provideServiceDisabilityAccommodationInfo() {
		int currNo = 1;
		int mergeResult = 0;
		int updateResult = 0;

		ArrayList<DisabilityAccommodationInfoDTO> infoList = new ArrayList<DisabilityAccommodationInfoDTO>();		
		HashMap<Integer, String> jsonMap = new HashMap<Integer, String>();
		
		// 1) contentId 조회 및 저장 (DB)
		ArrayList<Integer> contentIDList = disabilityAccommodationInfoDAO.getContentIdList();
		ArrayList<String> jsonList = new ArrayList<String>();

		// 2) contentid로 무장애정보조회(API) 후 String 리스트에 저장
		for (int contentID : contentIDList) {
			String json = disabilityAccommodationInfoRepos.selectdisabilityAccommodationInfoByAPI(contentID);
			if (json != null)
				jsonMap.put(contentID, json);
		}
		
		System.out.println("api 조회 완료");

		// 3) jsonList의 JSON 파싱
		if(!jsonMap.isEmpty())
			infoList = disabilityAccommodationInfoRepos.parseJson(jsonMap);
		
		System.out.println("파싱 완료");

		// 4) DB Merge
		if(infoList != null)
			mergeResult += disabilityAccommodationInfoDAO.saveDisabilityAccommodationInfo(infoList);
		
		// 5) TB_REQUEST_CHECK Table Update
		if(!jsonMap.isEmpty())
			updateResult += disabilityAccommodationInfoDAO.updateTableRequestCheck(jsonMap);
		
		// 5) 결과 확인
		System.out.println("Merge 수 : " + mergeResult);
		System.out.println("Update 수 : " + updateResult);

	}

}
