package com.example.demo.Service;
import com.example.demo.model.ActivityScore;
import com.example.demo.model.ActivityType;
import com.example.demo.Mapper.ActivityTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.*;

@Service
public class SubmitapplicationsectionService {
    @Autowired
    private ActivityTypeMapper activityTypeMapper;

    private LevenshteinDistance levenshtein = new LevenshteinDistance();

    public List<ActivityScore> processExcel(MultipartFile file) {
        List<ActivityScore> scores = new ArrayList<>();
        Map<String, List<String>> hierarchy = createActivityHierarchy();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            // 中文到英文的映射
            Map<String, String> categoryMapping = createCategoryMapping();
            Map<String, String> levelMapping = createLevelMapping();

            // 从第七行开始读取数据（索引为6）
            for (int rowIndex = 6; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue; // 跳过空行
                // 读取各列数据
                // 活动内容
                String activityContentChinese = getCellValue(row.getCell(4));
                String activityCategory1Chinese = getCellValue(row.getCell(5)); // 活动一级分类（中文）
                String activityCategory2Chinese = getCellValue(row.getCell(6)); // 活动二级分类（中文）
                String activityLevelChinese = getCellValue(row.getCell(7)); // 活动等级（中文）
                String awardsLevelChinese = getCellValue(row.getCell(8)); // 活动奖项（中文）

                String activityCategory1 = strictMatch(activityCategory1Chinese, categoryMapping); // 一级活动强匹配
                if (activityCategory1 == null) {
                    System.err.println("未找到一级分类的映射：" + activityCategory1Chinese);
                    continue;  // 终止该行的处理
                }

                String mergedContent = mergeFields(activityContentChinese, activityLevelChinese, awardsLevelChinese); // 合并字符串
                String activityCategory2 = matchSubCategory(
                        activityLevelChinese, awardsLevelChinese,
                        activityCategory2Chinese, mergedContent,
                        hierarchy
                );

                if (activityCategory2 == null) {
                    System.err.println("未找到匹配的二级分类：" + mergedContent);
                    continue;
                }







                // 从数据库中获取分数信息
                ActivityType activityType = activityTypeMapper.findByAtModuleAndAtName(activityCategory1, activityCategory2);
                if (activityType == null) {
                    System.err.println("未找到活动类型：" + activityCategory1 + ", " + activityCategory2);
                    continue; // 跳过映射失败的行
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scores;
    }

    private String matchSubCategory(
            String activityLevelChinese,
            String awardsLevelChinese,
            String activityCategory2Chinese,
            String mergedContent,
            Map<String, List<String>> hierarchy
    ) {
        List<String> subCategories = hierarchy.getOrDefault(activityLevelChinese, Collections.emptyList());

        // 优先在同等级下精确匹配
        for (String subCategory : subCategories) {
            if (subCategory.contains(activityCategory2Chinese) &&
                    subCategory.contains(activityLevelChinese) &&
                    subCategory.contains(awardsLevelChinese)) {
                return subCategory; // 返回精确匹配结果
            }
        }

        // 如果精确匹配失败，尝试在同等级下进行模糊匹配
        String bestMatch = null;
        int bestDistance = Integer.MAX_VALUE;
        for (String subCategory : subCategories) {
            int distance = levenshtein.apply(mergedContent, subCategory);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestMatch = subCategory;
            }
        }

        // 如果同等级下没有找到匹配结果，再尝试对传入的二级分类中文进行模糊匹配
        if (bestMatch == null) {
            for (String subCategory : subCategories) {
                int distance = levenshtein.apply(activityCategory2Chinese, subCategory);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestMatch = subCategory;
                }
            }
        }

        return bestMatch; // 返回匹配到的二级分类或 null
    }

    // 精确匹配：根据活动等级和获奖等级筛选二级分类
    private List<String> preciseMatch(String activityLevelChinese, String awardsLevelChinese,
                                      Map<String, List<String>> hierarchy) {
        List<String> matchedCategories = new ArrayList<>();

        for (List<String> subCategories : hierarchy.values()) {
            for (String subCategory : subCategories) {
                if (subCategory.contains(activityLevelChinese) && subCategory.contains(awardsLevelChinese)) {
                    matchedCategories.add(subCategory);
                }
            }
        }
        return matchedCategories;
    }

    // 模糊匹配逻辑
    private String fuzzyMatch(String input, List<String> subCategories) {
        String bestMatch = null;
        int bestDistance = Integer.MAX_VALUE;

        for (String subCategory : subCategories) {
            int distance = levenshtein.apply(input, subCategory);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestMatch = subCategory;
            }
        }
        return bestMatch;
    }
    private String fuzzyMatchCategory2(String mergedContent, String category1, Map<String, List<String>> hierarchy) {
        // 从层级结构中获取该一级分类对应的二级分类列表
        List<String> subCategories = hierarchy.getOrDefault(category1, new ArrayList<>());

        String bestMatch = null;
        int bestDistance = Integer.MAX_VALUE;

        // 在二级分类列表中进行模糊匹配
        for (String subCategory : subCategories) {
            int distance = levenshtein.apply(mergedContent, subCategory);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestMatch = subCategory;
            }
        }

        // 返回最佳匹配结果
        return bestMatch;
    }
    // 强匹配方法实现
    private String strictMatch(String input, Map<String, String> mapping) {
        // 直接匹配，找不到返回 null
        return mapping.getOrDefault(input, null);
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";  // 返回空字符串，避免 NullPointerException
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";  // 默认返回空字符串
        }
    }
    public void exportExcel(List<ActivityScore> scores) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Scores");
            for (ActivityScore score : scores) {
                Row row = sheet.createRow(scores.indexOf(score));
                row.createCell(0).setCellValue(score.getActivityName());
                row.createCell(1).setCellValue(score.getScore());
            }
            workbook.write(outputStream);
            // Here you can save the outputStream to a file or return it as a response
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String mergeFields(String... fields) { // 合并字符串
        StringBuilder merged = new StringBuilder();
        for (String field : fields) {
            if (!field.isEmpty()) {
                if (merged.length() > 0) merged.append(" ");
                merged.append(field);
            }
        }
        return merged.toString();
    }

    private int determineScore(String activityCategory1, String activityCategory2, String activityLevel, int creditValue) {
        // 实现根据分类和等级确定分数的逻辑
        // 这里可以结合你的需求进行调整，例如直接返回 creditValue
        return creditValue; // 假设直接返回发放的学分值
    }




    private Map<String, String> createCategoryMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("思想政治素养", "Ideological and political literacy");
        mapping.put("实践实习能力", "Practical and Internship Abilities");
        mapping.put("社会责任担当", "Social Responsibility");
        mapping.put("创新创业能力", "Innovative and Entrepreneurial Ability");
        mapping.put("文体素质拓展", "Cultural and Physical Quality Development");
        mapping.put("工作成长履历", "Work Growth History");
        mapping.put("专业技能认证", "Professional Skills Certification");
        mapping.put("其他", "Other");
        // 添加更多的映射关系
        return mapping;
    }
    private Map<String, String> createLevelMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("班级活动", "Class activities");
        mapping.put("院级活动", "Campus level activities");
        mapping.put("校级活动", "School level activities");
        mapping.put("参与竞赛", "Participate in the competition");
        mapping.put("院级一等奖", "First Prize at the Institute Level");
        mapping.put("院级二等奖", "Second Prize at the Academy Level");
        mapping.put("院级三等奖", "Third Prize at the Academy Level");
        mapping.put("院级优秀奖", "Academy level Excellent Award");
        mapping.put("校级一等奖", "First Prize at the School Level");
        mapping.put("校级二等奖", "Second Prize at the School Level");
        mapping.put("校级三等奖", "Third Prize at the School Level");
        mapping.put("校级优秀奖", "School level Excellent Award");
        mapping.put("省级一等奖", "Provincial First Prize");
        mapping.put("省级二等奖", "Provincial Second Prize");
        mapping.put("省级三等奖", "Provincial Third Prize");
        mapping.put("省级优秀奖", "Provincial Excellent Award");
        mapping.put("国家级一等奖", "National First Prize");
        mapping.put("国家级二等奖", "National Second Prize");
        mapping.put("国家级三等奖", "National Third Prize");
        mapping.put("国家级优秀奖", "National Excellent Award");
        mapping.put("院级培训合格", "Qualified in hospital level training");
        mapping.put("院级培训优秀", "Excellent training at the college level");
        mapping.put("校级培训合格", "Qualified in school level training");
        mapping.put("校级培训优秀", "Excellent school level training");
        mapping.put("省级培训合格", "Provincial level training qualified");
        mapping.put("省级培训优秀", "Excellent provincial training");
        mapping.put("国家级培训合格", "National level training qualified");
        mapping.put("国家级培训优秀", "Excellent national level training");
        mapping.put("参与实践", "Participation in Practice");
        mapping.put("院级立项团队负责人", "College-Level Project Team Leader");
        mapping.put("院级立项团队成员", "College-Level Project Team Member");
        mapping.put("校级立项团队负责人", "University-Level Project Team Leader");
        mapping.put("校级立项团队成员", "University-Level Project Team Member");
        mapping.put("国家表彰优秀团队", "National Commended Outstanding Team");
        mapping.put("省表彰优秀团队", "Provincial Commended Outstanding Team");
        mapping.put("校表彰优秀团队", "University Commended Outstanding Team");
        mapping.put("国家表彰优秀个人", "National Commended Outstanding Individual");
        mapping.put("省表彰优秀个人", "Provincial Commended Outstanding Individual");
        mapping.put("校表彰优秀个人", "University Commended Outstanding Individual");
        mapping.put("校级交流或访学", "University-Level Exchange or Study Visit");
        mapping.put("省级交流或访学", "Provincial-Level Exchange or Study Visit");
        mapping.put("国家级交流或访学", "National-Level Exchange or Study Visit");
        mapping.put("国际级交流或访学", "International-Level Exchange or Study Visit");
        mapping.put("公益活动1小时", "1 Hour of Public Welfare Activities");
        mapping.put("国家级公益活动荣誉", "National-Level Public Welfare Activity Honor");
        mapping.put("省级公益活动荣誉", "Provincial-Level Public Welfare Activity Honor");
        mapping.put("校级公益活动荣誉", "University-Level Public Welfare Activity Honor");
        mapping.put("无偿献血", "Voluntary Blood Donation");
        mapping.put("自主申报院级项目", "Self-Declared Institute-Level Projects");
        mapping.put("自主申报校级项目", "Self-Declared University-Level Projects");
        mapping.put("自主申报省级项目", "Self-Declared Provincial-Level Projects");
        mapping.put("自主申报国家级项目", "Self-Declared National-Level Projects");
        mapping.put("参与申报院级项目", "Participated in Applying for Institute-Level Projects");
        mapping.put("参与申报校级项目", "Participated in Applying for University-Level Projects");
        mapping.put("参与申报省级项目", "Participated in Applying for Provincial-Level Projects");
        mapping.put("参与申报国家级项目", "Participated in Applying for National-Level Projects");
        mapping.put("发明专利", "Invention Patent");
        mapping.put("实用新型专利", "Utility Model Patent");
        mapping.put("外观设计专利", "Design Patent");
        mapping.put("三大检索论文", "Three Major Retrieval Papers");
        mapping.put("核心期刊", "Core Journal");
        mapping.put("非核心期刊", "Non-Core Journal");
        mapping.put("校级原创文章", "University-Level Original Articles");
        mapping.put("院级原创文章", "Institute-Level Original Articles");
        mapping.put("国家报刊文章", "National Newspaper Articles");
        mapping.put("省报刊文章", "Provincial Newspaper Articles");
        mapping.put("州市报刊文章", "City Newspaper Articles");
        mapping.put("县报刊文章", "County Newspaper Articles");
        mapping.put("文艺类参与竞赛", "Art Participation Competitions");
        mapping.put("文艺类院级一等奖", "Art Institute-Level First Prize");
        mapping.put("文艺类院级二等奖", "Art Institute-Level Second Prize");
        mapping.put("文艺类院级三等奖", "Art Institute-Level Third Prize");
        mapping.put("文艺类院级优秀奖", "Art Institute-Level Excellence Award");
        mapping.put("文艺类校级一等奖", "Art University-Level First Prize");
        mapping.put("文艺类校级二等奖", "Art University-Level Second Prize");
        mapping.put("文艺类校级三等奖", "Art University-Level Third Prize");
        mapping.put("文艺类校级优秀奖", "Art University-Level Excellence Award");
        mapping.put("文艺类省级一等奖", "Art Provincial-Level First Prize");
        mapping.put("文艺类省级二等奖", "Art Provincial-Level Second Prize");
        mapping.put("文艺类省级三等奖", "Art Provincial-Level Third Prize");
        mapping.put("文艺类省级优秀奖", "Art Provincial-Level Excellence Award");
        mapping.put("文艺类国家级一等奖", "Art National-Level First Prize");
        mapping.put("文艺类国家级二等奖", "Art National-Level Second Prize");
        mapping.put("文艺类国家级三等奖", "Art National-Level Third Prize");
        mapping.put("文艺类国家级优秀奖", "Art National-Level Excellence Award");
        mapping.put("体育类参与竞赛", "Sports Participation Competitions");
        mapping.put("体育类院级一等奖", "Sports Institute-Level First Prize");
        mapping.put("体育类院级二等奖", "Sports Institute-Level Second Prize");
        mapping.put("体育类院级三等奖", "Sports Institute-Level Third Prize");
        mapping.put("体育类院级优秀奖", "Sports Institute-Level Excellence Award");
        mapping.put("体育类校级一等奖", "Sports University-Level First Prize");
        mapping.put("体育类校级二等奖", "Sports University-Level Second Prize");
        mapping.put("体育类校级三等奖", "Sports University-Level Third Prize");
        mapping.put("体育类校级优秀奖", "Sports University-Level Excellence Award");
        mapping.put("体育类省级一等奖", "Sports Provincial-Level First Prize");
        mapping.put("体育类省级二等奖", "Sports Provincial-Level Second Prize");
        mapping.put("体育类省级三等奖", "Sports Provincial-Level Third Prize");
        mapping.put("体育类省级优秀奖", "Sports Provincial-Level Excellence Award");
        mapping.put("体育类国家级一等奖", "Sports National-Level First Prize");
        mapping.put("体育类国家级二等奖", "Sports National-Level Second Prize");
        mapping.put("体育类国家级三等奖", "Sports National-Level Third Prize");
        mapping.put("体育类国家级优秀奖", "Sports National-Level Excellence Award");
        mapping.put("班长及团支书", "Class Monitor and Youth League Branch Secretary");
        mapping.put("主要班级学生干部", "Main Class Student Cadre");
        mapping.put("其他学生干部", "Other Student Cadres");
        mapping.put("党支部副书记", "Party Branch Deputy Secretary");
        mapping.put("支部委员", "Branch Committee Member");
        mapping.put("院级学生会主席、团委副书记", "Institute Student Union President, Youth League Deputy Secretary");
        mapping.put("院级学生副主席、秘书长", "Institute Student Vice President, Secretary General");
        mapping.put("院级各部门部长、副部长", "Institute Various Department Heads, Deputy Heads");
        mapping.put("院级干事", "Institute Cadres");
        mapping.put("校学生会主席、学生团委副书记、社团联合会主席", "University Student Union President, Youth League Deputy Secretary, Club Federation President");
        mapping.put("校学生会副主席、社联副主席", "University Student Union Vice President, Club Federation Vice President");
        mapping.put("校各部门部长、副部长", "University Various Department Heads, Deputy Heads");
        mapping.put("校干事", "University Cadres");
        mapping.put("校各部门助理类学生干部", "University Various Department Assistants");
        mapping.put("专业团体干事", "Professional Organization Cadres");
        mapping.put("专业性团体负责人", "Professional Organization Leaders");
        mapping.put("专业性团体其他负责人", "Other Professional Organization Leaders");
        mapping.put("社团活动会员", "Club Activity Members");
        mapping.put("社团主要负责人", "Club Main Leaders");
        mapping.put("社团其他负责人", "Other Club Leaders");
        mapping.put("国家先进集体等", "National Advanced Collective, etc.");
        mapping.put("省先进集体等", "Provincial Advanced Collective, etc.");
        mapping.put("校先进集体等", "University Advanced Collective, etc.");
        mapping.put("院先进集体等", "Institute Advanced Collective, etc.");
        mapping.put("院文明宿舍", "Institute Civilized Dormitory");
        mapping.put("校文明宿舍", "University Civilized Dormitory");
        mapping.put("五星级社团", "Five-Star Club");
        mapping.put("四星级社团", "Four-Star Club");
        mapping.put("三星级社团", "Three-Star Club");
        mapping.put("国家三好学生等", "National Model Student, etc.");
        mapping.put("省三好学生等", "Provincial Model Student, etc.");
        mapping.put("校三好学生等", "University Model Student, etc.");
        mapping.put("院三好学生等", "Institute Model Student, etc.");
        mapping.put("国家自强之星年度人物等", "National Self-Reliance Star of the Year, etc.");
        mapping.put("省自强之星年度人物等", "Provincial Self-Reliance Star of the Year, etc.");
        mapping.put("校级标兵", "University-Level Model");
        mapping.put("校级标兵提名奖", "University-Level Model Nomination Award");
        mapping.put("参加比赛", "Participation in Competitions");
        mapping.put("证书", "Certificates");
        // 添加更多的映射关系
        return mapping;
    }

    /**
     * 创建一级分类 -> 对应二级分类列表的层级结构
     */
    public Map<String, List<String>> createActivityHierarchy() {
        Map<String, List<String>> hierarchy = new HashMap<>();

        hierarchy.put("Ideological and political literacy", Arrays.asList(
                "Class activities", "Campus level activities", "School level activities",
                "Participate in the competition", "First Prize at the Institute Level",
                "Second Prize at the Academy Level", "Third Prize at the Academy Level",
                "Academy level Excellent Award", "First Prize at the School Level",
                "Second Prize at the School Level", "Third Prize at the School Level",
                "School level Excellent Award", "Provincial First Prize", "Provincial Second Prize",
                "Provincial Third Prize", "Provincial Excellent Award", "National First Prize",
                "National Second Prize", "National Third Prize", "National Excellent Award",
                "Qualified in hospital level training", "Excellent training at the college level",
                "Qualified in school level training", "Excellent school level training",
                "Provincial level training qualified", "Excellent provincial training",
                "National level training qualified", "Excellent national level training"
        ));

        hierarchy.put("Practical and Internship Abilities", Arrays.asList(
                "Class Activities", "College-Level Activities", "University-Level Activities",
                "Participation in Competitions", "College-Level First Prize",
                "College-Level Second Prize", "College-Level Third Prize",
                "College-Level Excellence Award", "University-Level First Prize",
                "University-Level Second Prize", "University-Level Third Prize",
                "University-Level Excellence Award", "Provincial-Level First Prize",
                "Provincial-Level Second Prize", "Provincial-Level Third Prize",
                "Provincial-Level Excellence Award", "National-Level First Prize",
                "National-Level Second Prize", "National-Level Third Prize",
                "National-Level Excellence Award", "Participation in Practice",
                "College-Level Project Team Leader", "College-Level Project Team Member",
                "University-Level Project Team Leader", "University-Level Project Team Member",
                "National Commended Outstanding Team", "Provincial Commended Outstanding Team",
                "University Commended Outstanding Team", "National Commended Outstanding Individual",
                "Provincial Commended Outstanding Individual", "University Commended Outstanding Individual",
                "University-Level Exchange or Study Visit", "Provincial-Level Exchange or Study Visit",
                "National-Level Exchange or Study Visit", "International-Level Exchange or Study Visit"
        ));

        hierarchy.put("Social Responsibility", Arrays.asList(
                "1 Hour of Public Welfare Activities", "National-Level Public Welfare Activity Honor",
                "Provincial-Level Public Welfare Activity Honor",
                "University-Level Public Welfare Activity Honor", "Voluntary Blood Donation"
        ));

        hierarchy.put("Innovative and Entrepreneurial Ability", Arrays.asList(
                "Class Activities", "Institute-Level Activities", "University-Level Activities",
                "Self-Declared Institute-Level Projects", "Self-Declared University-Level Projects",
                "Self-Declared Provincial-Level Projects", "Self-Declared National-Level Projects",
                "Participated in Applying for Institute-Level Projects",
                "Participated in Applying for University-Level Projects",
                "Participated in Applying for Provincial-Level Projects",
                "Participated in Applying for National-Level Projects",
                "Participation in Competitions", "Institute-Level First Prize",
                "Institute-Level Second Prize", "Institute-Level Third Prize",
                "Institute-Level Excellence Award", "University-Level First Prize",
                "University-Level Second Prize", "University-Level Third Prize",
                "University-Level Excellence Award", "Provincial-Level First Prize",
                "Provincial-Level Second Prize", "Provincial-Level Third Prize",
                "Provincial-Level Excellence Award", "National-Level First Prize",
                "National-Level Second Prize", "National-Level Third Prize",
                "National-Level Excellence Award", "Invention Patent",
                "Utility Model Patent", "Design Patent", "Three Major Retrieval Papers",
                "Core Journal", "Non-Core Journal", "University-Level Original Articles",
                "Institute-Level Original Articles", "National Newspaper Articles",
                "Provincial Newspaper Articles", "City Newspaper Articles",
                "County Newspaper Articles"
        ));

        hierarchy.put("Cultural and Physical Quality Development", Arrays.asList(
                "Art Participation Competitions", "Art Institute-Level First Prize",
                "Art Institute-Level Second Prize", "Art Institute-Level Third Prize",
                "Art Institute-Level Excellence Award", "Art University-Level First Prize",
                "Art University-Level Second Prize", "Art University-Level Third Prize",
                "Art University-Level Excellence Award", "Art Provincial-Level First Prize",
                "Art Provincial-Level Second Prize", "Art Provincial-Level Third Prize",
                "Art Provincial-Level Excellence Award", "Art National-Level First Prize",
                "Art National-Level Second Prize", "Art National-Level Third Prize",
                "Art National-Level Excellence Award", "Sports Participation Competitions",
                "Sports Institute-Level First Prize", "Sports Institute-Level Second Prize",
                "Sports Institute-Level Third Prize", "Sports Institute-Level Excellence Award",
                "Sports University-Level First Prize", "Sports University-Level Second Prize",
                "Sports University-Level Third Prize", "Sports University-Level Excellence Award",
                "Sports Provincial-Level First Prize", "Sports Provincial-Level Second Prize",
                "Sports Provincial-Level Third Prize", "Sports Provincial-Level Excellence Award",
                "Sports National-Level First Prize", "Sports National-Level Second Prize",
                "Sports National-Level Third Prize", "Sports National-Level Excellence Award"
        ));

        hierarchy.put("Work Growth History", Arrays.asList(
                "Class Monitor and Youth League Branch Secretary", "Main Class Student Cadre",
                "Other Student Cadres", "Party Branch Deputy Secretary", "Branch Committee Member",
                "Institute Student Union President, Youth League Deputy Secretary",
                "Institute Student Vice President, Secretary General",
                "Institute Various Department Heads, Deputy Heads", "Institute Cadres",
                "University Student Union President, Youth League Deputy Secretary, Club Federation President",
                "University Student Union Vice President, Club Federation Vice President",
                "University Various Department Heads, Deputy Heads", "University Cadres",
                "University Various Department Assistants", "Professional Organization Cadres",
                "Professional Organization Leaders", "Other Professional Organization Leaders",
                "Club Activity Members", "Club Main Leaders", "Other Club Leaders",
                "National Advanced Collective, etc.", "Provincial Advanced Collective, etc.",
                "University Advanced Collective, etc.", "Institute Advanced Collective, etc.",
                "Institute Civilized Dormitory", "University Civilized Dormitory",
                "Five-Star Club", "Four-Star Club", "Three-Star Club",
                "National Model Student, etc.", "Provincial Model Student, etc.",
                "University Model Student, etc.", "Institute Model Student, etc.",
                "National Self-Reliance Star of the Year, etc.",
                "Provincial Self-Reliance Star of the Year, etc.",
                "University-Level Model", "University-Level Model Nomination Award"
        ));

        hierarchy.put("Professional Skills Certification", Arrays.asList(
                "Class Activities", "Institute-Level Activities", "University-Level Activities",
                "Participation in Competitions", "Institute-Level First Prize",
                "Institute-Level Second Prize", "Institute-Level Third Prize",
                "Institute-Level Excellence Award", "University-Level First Prize",
                "University-Level Second Prize", "University-Level Third Prize",
                "University-Level Excellence Award", "Provincial-Level First Prize",
                "Provincial-Level Second Prize", "Provincial-Level Third Prize",
                "Provincial-Level Excellence Award", "National-Level First Prize",
                "National-Level Second Prize", "National-Level Third Prize",
                "National-Level Excellence Award"
        ));

        hierarchy.put("Other", Collections.singletonList("Certificates"));

        return hierarchy;
    }
}
