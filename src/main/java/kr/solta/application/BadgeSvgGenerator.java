package kr.solta.application;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BadgeSvgGenerator {

    // ── Layout constants ──────────────────────────────────────────────────────
    private static final int W = 400;
    private static final int H = 165;
    private static final int PAD = 14;
    private static final int HEADER_H = 38;
    private static final int DIV_X = 188;
    private static final int CHART_X = DIV_X + 16;
    private static final int CHART_R = W - PAD;
    private static final int CHART_W = CHART_R - CHART_X;
    private static final int C_BOTTOM = H - 20;
    private static final int C_TOP = HEADER_H + 36;
    private static final int MAX_BAR_H = C_BOTTOM - C_TOP;
    private static final int BAR_W = 14;
    private static final int CHART_GAP = 13;

    // ── Colours ───────────────────────────────────────────────────────────────
    private static final String TEXT = "#E4E6EB";
    private static final String TEXT_SUB = "#8B949E";
    private static final String DIVIDER = "rgba(255,255,255,0.09)";
    private static final String LOGO_A = "#FF9A76";
    private static final String LOGO_B = "#FF7C5C";
    private static final String BG_START = "#0D1117";
    private static final String BG_MID = "#142035";
    private static final String BG_END = "#1c3554";

    // ── Logo bar sizes ────────────────────────────────────────────────────────
    private static final int[] BAR_SIZES = {7, 11, 15};
    private static final int BAR_W_LOGO = 5;
    private static final int BAR_GAP = 3;
    private static final int BAR_BOTTOM = HEADER_H - 10;

    public record TierBarData(String label, int avgMinutes, String color) {}

    public String generate(
            String username,
            int totalMinutes,
            int avgMinutes,
            int selfSolveRate,
            List<TierBarData> tierData
    ) {
        String uid = "b" + Math.abs((username + totalMinutes + avgMinutes).hashCode());

        String bgId = "bg-" + uid;
        String glossId = "gloss-" + uid;
        String shimId = "shim-" + uid;
        String shimKf = "shimKf-" + uid;
        String clipId = "clip-" + uid;
        String logoGradId = "logo-" + uid;
        String wordmarkGradId = "wm-" + uid;

        int logoEndX = PAD + BAR_SIZES.length * BAR_W_LOGO + (BAR_SIZES.length - 1) * BAR_GAP;

        // ── Total time string ─────────────────────────────────────────────────
        int totalH = totalMinutes / 60;
        int totalM = totalMinutes % 60;
        String totalTimeStr;
        if (totalH > 0) {
            totalTimeStr = totalH + "시간 " + totalM + "분";
        } else {
            totalTimeStr = totalM + "분";
        }

        // ── Active tier bars ──────────────────────────────────────────────────
        List<TierBarData> active = tierData.stream()
                .filter(d -> d.avgMinutes() > 0)
                .toList();

        int rawMax = active.stream()
                .mapToInt(TierBarData::avgMinutes)
                .max()
                .orElse(60);
        int scalingMax = Math.min(Math.max(rawMax, 60), 300);

        int groupW = active.size() * BAR_W + (active.size() + 1) * CHART_GAP;
        double chartOffX = CHART_X + (double) (CHART_W - groupW) / 2;

        // ── Build SVG ─────────────────────────────────────────────────────────
        StringBuilder sb = new StringBuilder();

        sb.append("""
                <?xml version="1.0" encoding="UTF-8"?>
                """);

        sb.append(String.format(
                "<svg width=\"%d\" height=\"%d\" viewBox=\"0 0 %d %d\" xmlns=\"http://www.w3.org/2000/svg\">\n",
                W, H, W, H
        ));

        // Style / keyframes
        sb.append(String.format("""
                <style>
                  @keyframes %s {
                    0%%   { transform: translateX(-280px) skewX(-18deg); opacity: 0; }
                    6%%   { transform: translateX(-120px) skewX(-18deg); opacity: 1; }
                    28%%  { transform: translateX(%dpx) skewX(-18deg); opacity: 1; }
                    36%%  { transform: translateX(%dpx) skewX(-18deg); opacity: 0; }
                    100%% { transform: translateX(%dpx) skewX(-18deg); opacity: 0; }
                  }
                  .%s { animation: %s 5s linear infinite; }
                </style>
                """, shimKf, W - 40, W + 120, W + 120, shimId, shimKf));

        // Defs
        sb.append("<defs>\n");

        // Background gradient
        sb.append(String.format("""
                <linearGradient id="%s" x1="0%%" y1="0%%" x2="100%%" y2="100%%">
                  <stop offset="0%%" stop-color="%s"/>
                  <stop offset="50%%" stop-color="%s"/>
                  <stop offset="100%%" stop-color="%s"/>
                </linearGradient>
                """, bgId, BG_START, BG_MID, BG_END));

        // Gloss gradient
        sb.append(String.format("""
                <linearGradient id="%s" x1="0%%" y1="0%%" x2="0%%" y2="100%%">
                  <stop offset="0%%" stop-color="rgba(255,255,255,0.06)"/>
                  <stop offset="60%%" stop-color="rgba(255,255,255,0.01)"/>
                  <stop offset="100%%" stop-color="rgba(255,255,255,0)"/>
                </linearGradient>
                """, glossId));

        // Shimmer gradient
        sb.append(String.format("""
                <linearGradient id="%s-grad" x1="0%%" y1="0%%" x2="100%%" y2="0%%">
                  <stop offset="0%%" stop-color="rgba(255,255,255,0)"/>
                  <stop offset="30%%" stop-color="rgba(255,255,255,0.015)"/>
                  <stop offset="50%%" stop-color="rgba(255,255,255,0.025)"/>
                  <stop offset="70%%" stop-color="rgba(255,255,255,0.015)"/>
                  <stop offset="100%%" stop-color="rgba(255,255,255,0)"/>
                </linearGradient>
                """, shimId));

        // Logo gradient
        sb.append(String.format("""
                <linearGradient id="%s" gradientUnits="userSpaceOnUse"
                    x1="%d" y1="%d" x2="%d" y2="%d">
                  <stop offset="0%%" stop-color="%s"/>
                  <stop offset="100%%" stop-color="%s"/>
                </linearGradient>
                """, logoGradId, PAD, BAR_BOTTOM - 22, logoEndX, BAR_BOTTOM, LOGO_A, LOGO_B));

        // Wordmark gradient
        sb.append(String.format("""
                <linearGradient id="%s" gradientUnits="userSpaceOnUse"
                    x1="39" y1="0" x2="79" y2="0">
                  <stop offset="0%%" stop-color="%s"/>
                  <stop offset="100%%" stop-color="%s"/>
                </linearGradient>
                """, wordmarkGradId, LOGO_A, LOGO_B));

        // Clip path
        sb.append(String.format("""
                <clipPath id="%s">
                  <rect x="0" y="0" width="%d" height="%d" rx="12" ry="12"/>
                </clipPath>
                """, clipId, W, H));

        sb.append("</defs>\n");

        // Background rect
        sb.append(String.format(
                "<rect x=\"0\" y=\"0\" width=\"%d\" height=\"%d\" rx=\"12\" ry=\"12\" fill=\"url(#%s)\"/>\n",
                W, H, bgId
        ));

        // Gloss + shimmer
        sb.append(String.format("<g clip-path=\"url(#%s)\">\n", clipId));
        sb.append(String.format(
                "  <rect x=\"0\" y=\"0\" width=\"%d\" height=\"%d\" fill=\"url(#%s)\"/>\n",
                W, H, glossId
        ));
        sb.append(String.format(
                "  <rect class=\"%s\" x=\"-220\" y=\"0\" width=\"220\" height=\"%d\" fill=\"url(#%s-grad)\"/>\n",
                shimId, H, shimId
        ));
        sb.append("</g>\n");

        // Logo bars
        for (int i = 0; i < BAR_SIZES.length; i++) {
            int h = BAR_SIZES[i];
            int x = PAD + i * (BAR_W_LOGO + BAR_GAP);
            int y = BAR_BOTTOM - h;
            sb.append(String.format(
                    "<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" rx=\"1.5\" fill=\"url(#%s)\"/>\n",
                    x, y, BAR_W_LOGO, h, logoGradId
            ));
        }

        // Wordmark "Solta"
        int wordX = PAD + BAR_SIZES.length * (BAR_W_LOGO + BAR_GAP) + 1;
        sb.append(String.format(
                "<text x=\"%d\" y=\"%d\" font-family=\"Outfit,'Noto Sans KR',system-ui,sans-serif\" font-size=\"14\" font-weight=\"700\" fill=\"url(#%s)\" letter-spacing=\"0.5\">Solta</text>\n",
                wordX, BAR_BOTTOM - 2, wordmarkGradId
        ));

        // Username
        sb.append(String.format(
                "<text x=\"%d\" y=\"%d\" font-family=\"'Noto Sans KR',Outfit,system-ui,sans-serif\" font-size=\"11\" font-weight=\"400\" fill=\"%s\" text-anchor=\"end\">@%s</text>\n",
                W - PAD, BAR_BOTTOM - 2, TEXT_SUB, escapeXml(username)
        ));

        // Horizontal rule
        sb.append(String.format(
                "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"%s\" stroke-width=\"1\"/>\n",
                PAD, HEADER_H, W - PAD, HEADER_H, DIVIDER
        ));

        // ── Left panel ────────────────────────────────────────────────────────
        // 총 풀이 시간 label
        sb.append(String.format(
                "<text x=\"%d\" y=\"%d\" font-family=\"'Noto Sans KR',Outfit,system-ui,sans-serif\" font-size=\"9\" font-weight=\"400\" fill=\"%s\">총 풀이 시간</text>\n",
                PAD, HEADER_H + 34, TEXT_SUB
        ));
        // 총 풀이 시간 value
        sb.append(String.format(
                "<text x=\"%d\" y=\"%d\" font-family=\"'Noto Sans KR',Outfit,system-ui,sans-serif\" font-size=\"17\" font-weight=\"700\" fill=\"%s\">%s</text>\n",
                PAD, HEADER_H + 54, TEXT, escapeXml(totalTimeStr)
        ));

        // 평균 풀이 시간 label
        sb.append(String.format(
                "<text x=\"%d\" y=\"%d\" font-family=\"'Noto Sans KR',Outfit,system-ui,sans-serif\" font-size=\"9\" font-weight=\"400\" fill=\"%s\">평균 풀이 시간</text>\n",
                PAD, HEADER_H + 84, TEXT_SUB
        ));
        // 평균 풀이 시간 value
        sb.append(String.format(
                "<text x=\"%d\" y=\"%d\" font-family=\"'Noto Sans KR',Outfit,system-ui,sans-serif\" font-size=\"14\" font-weight=\"700\" fill=\"%s\">%d분</text>\n",
                PAD, HEADER_H + 99, TEXT, avgMinutes
        ));

        // 자력 해결률 label
        sb.append(String.format(
                "<text x=\"%d\" y=\"%d\" font-family=\"'Noto Sans KR',Outfit,system-ui,sans-serif\" font-size=\"9\" font-weight=\"400\" fill=\"%s\">자력 해결률</text>\n",
                PAD + 84, HEADER_H + 84, TEXT_SUB
        ));
        // 자력 해결률 value
        sb.append(String.format(
                "<text x=\"%d\" y=\"%d\" font-family=\"'Noto Sans KR',Outfit,system-ui,sans-serif\" font-size=\"14\" font-weight=\"700\" fill=\"%s\">%d%%</text>\n",
                PAD + 84, HEADER_H + 99, TEXT, selfSolveRate
        ));

        // Vertical divider
        sb.append(String.format(
                "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"%s\" stroke-width=\"1\"/>\n",
                DIV_X, HEADER_H + 10, DIV_X, H - 10, DIVIDER
        ));

        // ── Right panel: tier bar chart ───────────────────────────────────────
        sb.append(String.format(
                "<text x=\"%.1f\" y=\"%d\" font-family=\"'Noto Sans KR',Outfit,system-ui,sans-serif\" font-size=\"9\" font-weight=\"400\" fill=\"%s\">티어별 평균 풀이 시간</text>\n",
                (double)(CHART_X + 4), HEADER_H + 15, TEXT_SUB
        ));

        for (int i = 0; i < active.size(); i++) {
            TierBarData d = active.get(i);
            double barH = Math.min((double) d.avgMinutes() / scalingMax * MAX_BAR_H, MAX_BAR_H);
            double bX = chartOffX + CHART_GAP + i * (double) (BAR_W + CHART_GAP);
            double bY = C_BOTTOM - barH;
            double labelY = bY - 4;

            // bar rect
            sb.append(String.format(
                    "<rect x=\"%.1f\" y=\"%.1f\" width=\"%d\" height=\"%.1f\" rx=\"3\" fill=\"%s\" opacity=\"0.9\"/>\n",
                    bX, bY, BAR_W, barH, d.color()
            ));
            // value label above bar
            sb.append(String.format(
                    "<text x=\"%.1f\" y=\"%.1f\" font-family=\"'Noto Sans KR',Outfit,system-ui,sans-serif\" font-size=\"8\" font-weight=\"600\" fill=\"%s\" text-anchor=\"middle\" opacity=\"0.95\">%d분</text>\n",
                    bX + BAR_W / 2.0, labelY, d.color(), d.avgMinutes()
            ));
            // tier label below bar
            sb.append(String.format(
                    "<text x=\"%.1f\" y=\"%d\" font-family=\"'Noto Sans KR',Outfit,system-ui,sans-serif\" font-size=\"8\" font-weight=\"500\" fill=\"%s\" text-anchor=\"middle\">%s</text>\n",
                    bX + BAR_W / 2.0, C_BOTTOM + 12, TEXT_SUB, d.label()
            ));
        }

        sb.append("</svg>");

        return sb.toString();
    }

    private String escapeXml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
