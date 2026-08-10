package moadong.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moadong.club.entity.Club;
import moadong.club.enums.ClubCategory;
import moadong.club.enums.ClubDivision;
import moadong.club.payload.dto.ClubDescriptionDto;
import moadong.club.payload.request.ClubInfoRequest;
import moadong.club.repository.ClubRepository;
import moadong.user.entity.User;
import moadong.user.payload.request.DevRegisterRequest;
import moadong.user.payload.request.UserRegisterRequest;
import moadong.user.repository.UserRepository;
import moadong.user.service.UserCommandService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DummyDataInitializer implements CommandLineRunner {

    private final UserCommandService userCommandService;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    private record ClubSeed(String uid, String name, ClubCategory cat, String pres, List<String> tags, String intro) {}

    @Override
    public void run(String... args) throws Exception {
        if (clubRepository.count() >= 60) {
            log.info("Dummy data (>= 60 clubs) already exists. Skip initialization.");
            return;
        }

        log.info("Starting 60 dummy clubs initialization...");

        try {
            // 1. 개발자 계정 생성
            if (userRepository.findUserByUserId("devadmin").isEmpty()) {
                DevRegisterRequest devReq = new DevRegisterRequest("devadmin", "Admin1234!", "개발자", "010-1234-5678", "dummy-dev-secret");
                userCommandService.registerDeveloper(devReq);
                log.info("Dev account created: devadmin");
            }

            // 2. 60개 동아리 데이터 목록 (intro <= 24자, tags <= 3개, 각 tag <= 5자)
            List<ClubSeed> seeds = List.of(
                // 봉사 (10)
                new ClubSeed("club001", "봉사동아리", ClubCategory.봉사, "김봉사", List.of("봉사", "나눔", "사회"), "따뜻한 사회를 만드는 봉사동아리"),
                new ClubSeed("club007", "해바라기봉사단", ClubCategory.봉사, "나아동", List.of("봉사", "아동", "돌봄"), "아동센터 학습 보조 봉사단"),
                new ClubSeed("club008", "유기동물보호회", ClubCategory.봉사, "동보호", List.of("봉사", "유기견", "보호소"), "유기동물 정기 봉사활동 동아리"),
                new ClubSeed("club009", "환경정화단", ClubCategory.봉사, "송환경", List.of("봉사", "플로깅", "환경"), "해변 플로깅 환경 보호 봉사단"),
                new ClubSeed("club010", "교육봉사단멘토", ClubCategory.봉사, "류멘토", List.of("봉사", "멘토링", "청소년"), "청소년 학습 멘토링 봉사단"),
                new ClubSeed("club011", "사랑의연탄나눔", ClubCategory.봉사, "차나눔", List.of("봉사", "연탄", "독거노인"), "독거노인 연탄 생필품 지원 나눔단"),
                new ClubSeed("club012", "벽화봉사단디자인", ClubCategory.봉사, "신벽화", List.of("봉사", "벽화", "미술"), "골목길 알록달록 벽화 그려요"),
                new ClubSeed("club013", "다문화지원단", ClubCategory.봉사, "백문화", List.of("봉사", "다문화", "한국어"), "다문화 가정 한국어 교육 지원단"),
                new ClubSeed("club014", "헌혈사랑봉사단", ClubCategory.봉사, "홍헌혈", List.of("봉사", "헌혈", "기부"), "정기 헌혈 생명 나눔 봉사단"),
                new ClubSeed("club015", "재능기부음악단", ClubCategory.봉사, "서위로", List.of("봉사", "음악", "재능기부"), "요양원 방문 음악 위로 봉사단"),

                // 학술 (10)
                new ClubSeed("club002", "IT연구회", ClubCategory.학술, "이학술", List.of("IT", "개발", "코딩"), "최신 기술을 연구하는 학술동아리"),
                new ClubSeed("club016", "AI데이터랩", ClubCategory.학술, "김인공", List.of("학술", "AI", "빅데이터"), "인공지능 데이터 분석 학술동아리"),
                new ClubSeed("club017", "알고리즘마스터", ClubCategory.학술, "박알고", List.of("학술", "코테", "알고리즘"), "코딩 테스트 알고리즘 스터디"),
                new ClubSeed("club018", "로봇공학회", ClubCategory.학술, "최로봇", List.of("학술", "로봇", "하드웨어"), "나만의 로봇을 제작하는 학술동아리"),
                new ClubSeed("club019", "경제경영연구회", ClubCategory.학술, "정경영", List.of("학술", "경제", "경영"), "글로벌 경제 경영 연구 학술동아리"),
                new ClubSeed("club020", "금융투자학회", ClubCategory.학술, "한금융", List.of("학술", "주식", "금융"), "모의 주식 투자 연구 학술동아리"),
                new ClubSeed("club021", "스타트업랩", ClubCategory.학술, "윤창업", List.of("학술", "창업", "스타트업"), "창업 경진대회 준비 학술동아리"),
                new ClubSeed("club022", "천문관측회", ClubCategory.학술, "강우주", List.of("학술", "천문", "관측"), "밤하늘 별자리 관측 학술동아리"),
                new ClubSeed("club023", "역사탐구회", ClubCategory.학술, "조역사", List.of("학술", "역사", "답사"), "유적 답사와 역사 연구 학술동아리"),
                new ClubSeed("club024", "생명과학연구회", ClubCategory.학술, "임바이오", List.of("학술", "바이오", "실험"), "바이오 기술 탐구 학술동아리"),

                // 공연 (10)
                new ClubSeed("club003", "댄스팀", ClubCategory.공연, "박공연", List.of("댄스", "공연", "무대"), "무대 위에서 빛나는 댄스동아리"),
                new ClubSeed("club005", "음악밴드", ClubCategory.공연, "정음악", List.of("음악", "밴드", "기타"), "함께 연주하는 밴드동아리"),
                new ClubSeed("club025", "스트릿댄스크루", ClubCategory.공연, "오스트릿", List.of("공연", "힙합", "팝핀"), "힙합 스트릿 댄스 전문 공연팀"),
                new ClubSeed("club026", "통기타울림", ClubCategory.공연, "황통기타", List.of("공연", "통기타", "어쿠스틱"), "감성 통기타 보컬 공연동아리"),
                new ClubSeed("club027", "극단무대", ClubCategory.공연, "문연극", List.of("공연", "연극", "연기"), "직접 연극 무대를 올리는 동아리"),
                new ClubSeed("club028", "뮤지컬컴퍼니", ClubCategory.공연, "양뮤지컬", List.of("공연", "뮤지컬", "노래"), "명작 뮤지컬 무대를 만드는 동아리"),
                new ClubSeed("club029", "아카펠라하모니", ClubCategory.공연, "변화음", List.of("공연", "아카펠라", "보컬"), "목소리 화음 아카펠라 공연동아리"),
                new ClubSeed("club030", "풍물패신명", ClubCategory.공연, "하전통", List.of("공연", "풍물", "사물놀이"), "신명 나는 전통 사물놀이 동아리"),
                new ClubSeed("club031", "재즈앙상블", ClubCategory.공연, "고재즈", List.of("공연", "재즈", "피아노"), "재즈 곡을 합주하는 공연동아리"),
                new ClubSeed("club032", "힙합클럽비트", ClubCategory.공연, "남힙합", List.of("공연", "힙합", "비트"), "비트제작과 랩 가사 만드는 동아리"),

                // 운동 (10)
                new ClubSeed("club004", "등산클럽", ClubCategory.운동, "최운동", List.of("등산", "운동", "자연"), "자연과 함께하는 등산동아리"),
                new ClubSeed("club033", "풋살매니아", ClubCategory.운동, "노풋살", List.of("운동", "풋살", "축구"), "매주 정기 풋살 경기 운동동아리"),
                new ClubSeed("club034", "농구동아리슬램", ClubCategory.운동, "하농구", List.of("운동", "농구", "코트"), "스피디한 농구 매치 운동동아리"),
                new ClubSeed("club035", "테니스아카데미", ClubCategory.운동, "곽테니스", List.of("운동", "테니스", "라켓"), "테니스 초보부터 즐기는 동아리"),
                new ClubSeed("club036", "배드민턴클럽", ClubCategory.운동, "성민턴", List.of("운동", "배드민턴", "셔틀콕"), "쉽고 신나는 배드민턴 운동동아리"),
                new ClubSeed("club037", "클라이밍크루", ClubCategory.운동, "배암벽", List.of("운동", "볼더링", "암벽등반"), "실내 볼더링 암벽등반 운동동아리"),
                new ClubSeed("club038", "수영사랑", ClubCategory.운동, "전수영", List.of("운동", "수영", "체력"), "정기 수영 함께하는 운동동아리"),
                new ClubSeed("club039", "탁구교실", ClubCategory.운동, "유탁구", List.of("운동", "탁구", "핑퐁"), "빠른 템포의 스매시 탁구 동아리"),
                new ClubSeed("club040", "러닝크루", ClubCategory.운동, "심러닝", List.of("운동", "러닝", "조깅"), "공원을 달리는 페이스 러닝 크루"),
                new ClubSeed("club041", "볼링클럽", ClubCategory.운동, "주볼링", List.of("운동", "볼링", "스트라이크"), "스트라이크 쾌감 나누는 볼링동아리"),

                // 취미교양 (10)
                new ClubSeed("club006", "사진동아리", ClubCategory.취미교양, "강사진", List.of("사진", "카메라", "출사"), "순간을 기록하는 사진동아리"),
                new ClubSeed("club042", "보드게임연구회", ClubCategory.취미교양, "원보드", List.of("보드게임", "전략", "친목"), "전략 보드게임 다양하게 즐기는 곳"),
                new ClubSeed("club043", "베이킹클럽", ClubCategory.취미교양, "천디저트", List.of("디저트", "쿠키", "제빵"), "디저트와 빵을 구워 나누는 동아리"),
                new ClubSeed("club044", "영화비평회", ClubCategory.취미교양, "방영화", List.of("영화", "시네마", "토론"), "명작 영화 관람 토론 취미동아리"),
                new ClubSeed("club045", "캘리그라피손글씨", ClubCategory.취미교양, "공글씨", List.of("손글씨", "붓펜", "감성"), "예쁜 글귀 손글씨 작품 동아리"),
                new ClubSeed("club046", "여행크루떠나자", ClubCategory.취미교양, "현여행", List.of("여행", "맛집", "식도락"), "전국 명소 맛집 투어 여행 크루"),
                new ClubSeed("club047", "E스포츠연구회", ClubCategory.취미교양, "지게임", List.of("게임", "롤", "발로란트"), "롤 발로란트 대회 개최 게임동아리"),
                new ClubSeed("club048", "애니메이션연구회", ClubCategory.취미교양, "도애니", List.of("애니", "덕질", "굿즈"), "인기 애니 감상 굿즈 교환 동아리"),
                new ClubSeed("club049", "도예공방", ClubCategory.취미교양, "진도예", List.of("도자기", "물레", "공예"), "나만의 머그컵 그릇 만드는 동아리"),
                new ClubSeed("club050", "마술연구회", ClubCategory.취미교양, "엄마술", List.of("마술", "트릭", "카드"), "스트릿 카드 마술 연구 동아리"),

                // 종교 (10)
                new ClubSeed("club051", "기독교연합CCC", ClubCategory.종교, "이믿음", List.of("종교", "기독교", "큐티"), "신앙으로 모이는 기독교 동아리"),
                new ClubSeed("club052", "가톨릭학생회", ClubCategory.종교, "김천주", List.of("종교", "천주교", "미사"), "성당 미사 신앙 모임 천주교 동아리"),
                new ClubSeed("club053", "불교학생회", ClubCategory.종교, "박불교", List.of("종교", "불교", "명상"), "참선 명상과 템플스테이 불교 동아리"),
                new ClubSeed("club054", "IVF기독학생회", ClubCategory.종교, "최신앙", List.of("종교", "기독교", "성경공부"), "성경 공부와 삶의 나눔 기독 동아리"),
                new ClubSeed("club055", "JOY선교회", ClubCategory.종교, "정기쁨", List.of("종교", "기독교", "찬양"), "기쁨의 신앙 공동체 종교 동아리"),
                new ClubSeed("club056", "성경탐구회", ClubCategory.종교, "한말씀", List.of("종교", "말씀", "성경"), "성경 읽고 적용하는 종교 동아리"),
                new ClubSeed("club057", "찬양동아리글로리아", ClubCategory.종교, "윤찬양", List.of("종교", "찬양", "워십"), "악기와 보컬 예배 찬양 동아리"),
                new ClubSeed("club058", "SFC학생신앙운동", ClubCategory.종교, "강신앙", List.of("종교", "개혁주의", "교제"), "학생 신앙 운동 종교 동아리"),
                new ClubSeed("club059", "마음챙김명상회", ClubCategory.종교, "조힐링", List.of("종교", "명상", "힐링"), "힐링 평안 마음챙김 명상 동아리"),
                new ClubSeed("club060", "원불교학생회", ClubCategory.종교, "임은혜", List.of("종교", "원불교", "마음공부"), "마음공부와 은혜 나눔 원불교 동아리")
            );

            int createdCount = 0;
            for (ClubSeed seed : seeds) {
                if (createClubDummy(seed.uid(), seed.name(), seed.cat(), ClubDivision.중동, seed.pres(), seed.tags(), seed.intro())) {
                    createdCount++;
                }
            }

            log.info("Dummy data initialization completed successfully! Created: {}, Total in DB: {}", createdCount, clubRepository.count());
        } catch (Exception e) {
            log.error("Failed to initialize dummy data: {}", e.getMessage(), e);
        }
    }

    private boolean createClubDummy(String uid, String name, ClubCategory cat, ClubDivision div, String pres, List<String> tags, String intro) {
        if (userRepository.findUserByUserId(uid).isPresent()) {
            return false;
        }
        // 회원 가입
        UserRegisterRequest regReq = new UserRegisterRequest(uid, "Club1234!", pres, "010-0000-0001");
        User user = userCommandService.registerUser(regReq);
        
        // 동아리 정보 업데이트
        Club club = clubRepository.findClubByUserId(user.getId()).orElseThrow();
        ClubDescriptionDto descDto = new ClubDescriptionDto(intro, name + " 활동 및 정기 모임 소개", Collections.emptyList(), null, null, Collections.emptyList());
        ClubInfoRequest infoReq = new ClubInfoRequest(
                name, cat, div, tags, intro, pres, descDto, "010-0000-0001", Map.of()
        );
        club.update(infoReq);
        clubRepository.save(club);
        log.info("Club created: {} ({})", name, cat);
        return true;
    }
}
