package org.fenixedu.oddjet.test.document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fenixedu.oddjet.Template;
import org.fenixedu.oddjet.exception.IllegalTemplateParameterNameException;
import org.fenixedu.oddjet.table.ListTableData;
import org.fenixedu.oddjet.table.PositionalTableData;
import org.fenixedu.oddjet.test.bean.Observation;
import org.fenixedu.oddjet.test.bean.ProgramCurricularUnit;

public class DiplomaSupplement extends Template {

    public DiplomaSupplement(String odtFilePath) throws SecurityException, IOException {
        super(odtFilePath);
        try {
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

            List<Object> program = new ArrayList<>();
            Observation obs = new Observation("1)", "Waka Waka");
            program.add(new ProgramCurricularUnit("2000/01", "Mais Materiais e cenas", "normal", "semestre", 6.0, 16, 16, obs
                    .getId()));
            addTableDataSource("program", new ListTableData(program));

            List<Object> observations = new ArrayList<>();
            observations.add(obs);
            addTableDataSource("program_observations", new ListTableData(observations));

            addParameter("classif_system_explanation", "É tipo uma escala de A a E com percentagens correspondentes e assim.");

            String[][] classifSystem =
                    { { "50%", "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%" },
                            { "D", "D", "D", "C", "C", "C", "B", "B", "B", "A", "A" } };
            addTableDataSource("classif_system", new PositionalTableData(classifSystem));

            addParameter("final_average", "16");
            addParameter("final_average_qualitative", "B");
            addParameter("final_average_ECTS", "6.0");
            addParameter("thesis_grade", "16");

            addParameter("higher_study_level_access", "Yup");
            addParameter("professional_statute", "Operador de Caixa");

            String[][] activities = { { "Beer Pong" } };
            addTableDataSource("extracurricular_activities", new PositionalTableData(activities));

            addParameter("authenticator_name", "Tonecas");
            addParameter("authenticator_position", "Palhaço");
        } catch (IllegalTemplateParameterNameException e) {
            e.printStackTrace();
        }

    }

}
