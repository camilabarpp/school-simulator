package br.com.gomining.schoolsimulator.common.exception.util;

public class ErrorMessage {
    public static final String ACTIVITY_NOT_FUND_WITH_ID = "Atividade não encontrada com o ID:";
    public static final String ACTIVITY_WITHOUT_GRADES = "A atividade não possui notas.";
    public static final String STUDENT_WITHOUT_GRADES_IN_ACTIVITY = "O estudante não possui notas nessa atividade.";
    public static final String STUDENT_WITHOUT_ACTIVITIES = "O estudante não possui atividades.";
    public static final String STUDENT_NOT_FOUND_BY_ID = "Estudante não encontrado com o ID: ";
    public static final String ACTIVITY_ALREADY_ADDED = "Atividade já adicionada ao estudante.";
    public static final String STUDENT_NOT_FOUND_BY_CPF_EMAIL_PHONE = "Estudante não encontrado com o CPF, email ou telefone: ";
    public static final String NO_REGISTERED_STUDENTS = "Não há estudantes cadastrados.";
    public static final String GRADE_NOT_FOUND_WITH_ID = "Nota não encontrada com o ID: ";

    private ErrorMessage(){}
}
