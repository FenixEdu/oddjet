package org.fenixedu.oddjet.test.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fenixedu.oddjet.TableData;
import org.fenixedu.oddjet.Template;

public class DiplomaSupplement extends Template {

    public DiplomaSupplement(String odtFilePath) {
        super(odtFilePath);
    }

    @Override
    protected void populate() {
        addParameter("full_name", "Manuel dos Santos");
        addParameter("family_names", "dos Santos");
        addParameter("given_names", "Manuel");
        addParameter("birth_date", "12/12/1982");
        addParameter("nationality", "portuguesa");
        addParameter("ID_doc_type", "C.C.");
        addParameter("ID_number", "12323421");
        addParameter("ID_expiration", "01/07/2015");
        addParameter("number", "78030");

        addParameter("degree_title", "Engenharia de Materiais");
        addParameter("scientific_area", "Materiais");
        addParameter("university_name", "Universidade de Lisboa");
        addParameter("university_statute", "universidade");
        addParameter("institution_name", "Técnico de Lisboa");
        addParameter("institution_statute", "instituto");
        addParameter("languages", "Português");

        addParameter("qualification_level", "alto");
        addParameter("years", "5");
        addParameter("semesters", "10");
        addParameter("weeks_per_year", "40");
        addParameter("credits", "180.0");

        addParameter("hasThesis", "YES");
        addParameter("isPHD", "NO");
        addParameter("study_regime", "no");
        addParameter("program_requirements", "nenhuns");

        //here is a reason to enable tables where the categories names would be used to access the objects' atributes.
        HashMap<String, List<Object>> tdata = new HashMap<>();
        List<Object> year = new ArrayList<>();
        year.add("2000/01");
        List<Object> unit = new ArrayList<>();
        unit.add("Mais Materiais e cenas");
        List<Object> type = new ArrayList<>();
        type.add("normal");
        List<Object> duration = new ArrayList<>();
        duration.add("semestre");
        List<Object> credits = new ArrayList<>();
        credits.add("6.0");
        List<Object> oclassif = new ArrayList<>();
        oclassif.add("16");
        List<Object> cclassif = new ArrayList<>();
        cclassif.add("16");
        List<Object> obs = new ArrayList<>();
        obs.add("1)");
        tdata.put("year", year);
        tdata.put("curricular unit", unit);
        tdata.put("type", type);
        tdata.put("duration", duration);
        tdata.put("credits", credits);
        tdata.put("obtained classification", oclassif);
        tdata.put("converted classification", cclassif);
        tdata.put("observations", obs);
        addTableDataSource("program", new TableData(tdata));

        tdata = new HashMap<>();
        List<Object> id = new ArrayList<>();
        id.add("1)");
        List<Object> label = new ArrayList<>();
        label.add("Waka Waka");
        tdata.put("id", id);
        tdata.put("label", label);
        addTableDataSource("program_labels", new TableData(tdata));

        addParameter(
                "classif_system_explanation",
                "É tipo uma escala de A a E com percentagens correspondentes e assim.\nVou saltar esta tabela, no fundo não interessa muito para o teste.");

        tdata = new HashMap<>();
        List<Object> percentage = new ArrayList<>();
        List<Object> ECTSScale = new ArrayList<>();
        tdata.put("percentage", percentage);
        tdata.put("ECTS scale", ECTSScale);
        addTableDataSource("classif_system", new TableData(tdata));

        addParameter("final_average", "16");
        addParameter("final_average_qualitative", "B");
        addParameter("final_average_ECTS", "6.0");
        addParameter("thesis_grade", "16");

        addParameter("higher_study_level_access", "Yup");
        addParameter("professional_statute", "Operador de Caixa");

        tdata = new HashMap<>();
        List<Object> activities = new ArrayList<>();
        activities.add("Beer Pong");
        tdata.put("activities", activities);
        addTableDataSource("extracurricular_activities", new TableData(tdata));

        addParameter("authenticator_name", "Tonecas");
        addParameter("authenticator_position", "Palhaço");

    }

    @Override
    public String getReportFileName() {
        return "Diploma Supplement";
    }

}
