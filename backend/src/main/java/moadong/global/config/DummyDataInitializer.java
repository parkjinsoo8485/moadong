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

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Dummy data already exists. Skip initialization.");
            return;
        }
        
        log.info("Starting dummy data initialization...");
        
        try {
            // 1. 개발자 계정 생성
            DevRegisterRequest devReq = new DevRegisterRequest("devadmin", "Admin1234!", "개발자", "010-1234-5678", "dummy-dev-secret");
            userCommandService.registerDeveloper(devReq);
            log.info("Dev account created: devadmin");

            // 2. 동아리 데이터 생성
            createClubDummy("club001", "봉사동아리", ClubCategory.봉사, ClubDivision.중동, "김봉사", List.of("봉사", "나눔", "사회"), "따뜻한 사회를 만드는 봉사동아리");
            createClubDummy("club002", "IT연구회", ClubCategory.학술, ClubDivision.중동, "이학술", List.of("IT", "개발", "코딩"), "최신 기술을 연구하는 학술동아리");
            createClubDummy("club003", "댄스팀", ClubCategory.공연, ClubDivision.중동, "박공연", List.of("댄스", "공연", "무대"), "무대 위에서 빛나는 댄스동아리");
            createClubDummy("club004", "등산클럽", ClubCategory.운동, ClubDivision.중동, "최운동", List.of("등산", "운동", "자연"), "자연과 함께하는 등산동아리");
            createClubDummy("club005", "음악밴드", ClubCategory.공연, ClubDivision.중동, "정음악", List.of("음악", "밴드", "기타"), "함께 연주하는 밴드동아리");
            createClubDummy("club006", "사진동아리", ClubCategory.취미교양, ClubDivision.중동, "강사진", List.of("사진", "카메라", "출사"), "순간을 기록하는 사진동아리");

            log.info("Dummy data initialization completed successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize dummy data: {}", e.getMessage(), e);
        }
    }

    private void createClubDummy(String uid, String name, ClubCategory cat, ClubDivision div, String pres, List<String> tags, String intro) {
        // 회원 가입
        UserRegisterRequest regReq = new UserRegisterRequest(uid, "Club1234!", pres, "010-0000-0001");
        User user = userCommandService.registerUser(regReq);
        
        // 동아리 정보 업데이트
        Club club = clubRepository.findClubByUserId(user.getId()).orElseThrow();
        ClubDescriptionDto descDto = new ClubDescriptionDto(intro, name + " 활동", Collections.emptyList(), null, null, Collections.emptyList());
        ClubInfoRequest infoReq = new ClubInfoRequest(
                name, cat, div, tags, intro, pres, descDto, "010-0000-0001", Map.of()
        );
        club.update(infoReq);
        clubRepository.save(club);
        log.info("Club created: {}", name);
    }
}
