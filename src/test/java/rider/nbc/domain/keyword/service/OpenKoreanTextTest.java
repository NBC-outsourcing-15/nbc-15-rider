package rider.nbc.domain.keyword.service;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;

import scala.collection.Seq;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 25.
 */
public class OpenKoreanTextTest {
	public static List<String> extractNouns(String text) {
		// 1. 정규화
		CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);

		// 2. 토큰화
		Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);

		// 3. Java List로 변환
		List<KoreanTokenJava> tokenList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);

		// 4. 명사만 추출
		return tokenList.stream()
			.filter(token -> token.getPos().toString().equals("Noun"))
			.map(KoreanTokenJava::getText)
			.collect(Collectors.toList());
	}

	@Test
	@DisplayName("형태소 분석기 테스트")
	void oktTest() {
		String text = "안녕하세요. 반갑습니다!";
		// 텍스트 정규화
		CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);
		// 토큰화
		Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
		// 결과 출력
		System.out.println(tokens);
	}

	@Test
	@DisplayName("명사만 추출 하기")
	void test1() {
		// 키워드는 미리 가지고 있음
		String[] testIntroductions = {
			"신선한 재료로 만든 수제 돈가스 전문점입니다.",
			"매일 아침 직접 끓인 육수로 깊은 맛을 내는 국밥집입니다.",
			"100% 국내산 쌀만 사용하는 김밥 전문 분식집입니다.",
			"비법 양념으로 맛을 낸 매운 닭발 맛집입니다.",
			"정성 가득한 엄마의 손맛을 담은 도시락 가게입니다.",
			"신선한 해산물로 만든 해물파전과 전 종류가 인기입니다.",
			"숙성 고기로 풍미를 살린 삼겹살 전문 고깃집입니다.",
			"방앗간에서 직접 뽑은 떡으로 만든 떡볶이 집입니다.",
			"진한 치즈 풍미의 수제 버거 전문점입니다.",
			"정직한 재료와 정성으로 만든 건강한 샐러드 가게입니다.",
			"싱싱한 회와 초밥을 즐길 수 있는 오마카세 전문점입니다.",
			"손으로 직접 빚은 수제 만두가 인기인 맛집입니다.",
			"뜨끈한 국물이 일품인 감자탕 전문점입니다.",
			"고소한 참기름 향이 가득한 비빔밥 맛집입니다.",
			"한우만 사용하는 프리미엄 육회비빔밥 전문점입니다.",
			"24시간 끓인 사골로 만든 설렁탕 전문점입니다.",
			"야식으로 최고인 마늘보쌈과 족발 전문점입니다.",
			"집밥처럼 따뜻한 한상차림을 제공하는 백반집입니다.",
			"카레와 돈가스를 한번에 즐길 수 있는 일식집입니다.",
			"채식주의자를 위한 비건 파스타 전문점입니다.",
			"불향 가득한 직화 불고기 덮밥 전문점입니다.",
			"쫀득한 면발과 매운 양념이 매력적인 국수집입니다.",
			"달콤한 디저트와 커피를 함께 즐길 수 있는 브런치 카페입니다.",
			"치킨과 맥주의 황금 비율을 자랑하는 치맥 맛집입니다.",
			"신선한 채소와 곡물로 만든 건강 도시락 전문점입니다.",
			"화덕에서 구운 정통 나폴리 피자 전문점입니다.",
			"수타면으로 만든 짬뽕과 짜장면이 인기인 중식당입니다.",
			"달콤한 허니버터와 바삭한 감자가 조화로운 감자 전문점입니다.",
			"전통 방식으로 담근 김치와 함께 즐기는 김치찌개집입니다.",
			"고급 원두를 직접 로스팅하는 커피 전문점입니다.",
			"정통 프랑스풍 크로와상과 빵을 만드는 베이커리입니다.",
			"버섯과 해산물이 어우러진 깊은 맛의 전골 전문점입니다.",
			"수제 양념으로 하루 숙성한 고등어구이 전문점입니다.",
			"정성껏 우려낸 육수로 만든 라멘 전문점입니다.",
			"재래시장에서 갓 구운 어묵과 떡볶이 맛집입니다.",
			"숯불 향 가득한 닭갈비 전문점입니다.",
			"달큰한 양념의 간장게장이 인기인 해산물집입니다.",
			"정통 멕시코 스타일 타코와 부리또를 즐길 수 있습니다.",
			"차돌박이 된장찌개가 일품인 식당입니다.",
			"매콤한 쭈꾸미와 볶음밥이 인기인 맛집입니다.",
			"아메리카노와 함께하는 수제 베이글 카페입니다.",
			"할머니의 손맛을 그대로 재현한 잔치국수집입니다.",
			"들기름 향이 고소한 메밀국수 전문점입니다.",
			"쫄깃한 면과 시원한 육수가 어우러진 냉면집입니다.",
			"즉석에서 구워내는 핫도그 전문점입니다.",
			"양념이 잘 배인 등갈비가 일품인 고깃집입니다.",
			"중독성 있는 매운 떡볶이와 순대가 인기입니다.",
			"계란찜과 함께 나오는 고등어 조림 전문점입니다.",
			"버터향 가득한 오믈렛과 토스트가 인기입니다.",
			"매일 아침 갓 구운 빵을 파는 동네 베이커리입니다."
		};

		for (String intro : testIntroductions) {
			List<String> keywords = extractNouns(intro).stream()
				.filter(keyword -> keyword.length() >= 2 && keyword.length() <= 5)
				.toList();

			System.out.printf("%s \n 키워드: %s\n\n", intro, keywords);
		}

		// 이름, 소개
		// 구현 의도 이름, 가게 소게 기반으로 키워드 구성
		// String introduce = "국내산 재료만 사용하는 정직한 분식집입니다.";
		// List<String> nouns = extractNouns(introduce);
		// System.out.println(nouns); // [국내, 산, 재료, 사용, 분식집]
	}
}
