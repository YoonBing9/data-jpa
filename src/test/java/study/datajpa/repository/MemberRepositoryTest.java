package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;
import study.datajpa.domain.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Rollback(false)
@Transactional
@SpringBootTest
class MemberRepositoryTest {
    @Autowired private MemberRepository memberRepository;
    @PersistenceContext private EntityManager em;

    @Test
    public void 회원_저장() throws Exception {
        //given
        Member createdMember = Member.createMember("윤병현", 33, Team.createTeam("teamA"));

        //when
        Member savedMember = memberRepository.save(createdMember);

        //then
        assertThat(createdMember).isEqualTo(savedMember);
    }

    @Test
    public void 회원_수정() throws Exception {
        //given
        Member createdMember = Member.createMember("윤병현", 33, Team.createTeam("teamA"));
        Member savedMember = memberRepository.save(createdMember);

        //when
        savedMember.changeAge(35);

        //then
        assertThat(35).isEqualTo(memberRepository.findById(createdMember.getId()).get().getAge());
    }

    @Test
    public void 회원_삭제() throws Exception {
        //given
        Member createdMember = Member.createMember("윤병현", 33, Team.createTeam("teamA"));
        Member savedMember = memberRepository.save(createdMember);
        Long savedMemberId = savedMember.getId();

        //when
        memberRepository.delete(savedMember);

        //then
        assertThat(memberRepository.findById(savedMemberId).isEmpty()).isTrue();
    }
}