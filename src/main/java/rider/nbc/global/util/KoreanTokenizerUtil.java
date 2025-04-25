package rider.nbc.global.util;

import java.util.List;
import java.util.stream.Collectors;

import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;

import scala.collection.Seq;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 25.
 */
public class KoreanTokenizerUtil {
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
}
