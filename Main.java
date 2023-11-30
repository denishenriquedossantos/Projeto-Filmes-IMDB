import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class Main {
    public static void main(String[] args) {
        //Link utilizado na conexão do banco de dados
        String jdbcurl = "jdbc:mysql://localhost:3306/?zeroDateTimeBehavior=convertToNull";
        //Nome do usuário e senha para acessar o banco de dados
        String usuario = "user";
        String senha = "password";

        //Caminho do arquivo de fonte de dados
        String csvPath = "imdb_top_1000.csv";

        //Quantidade de declarações SQL a serem executadas em lote
        int batchSize = 20;

        Connection connection = null;

        //Leitor e processador dos dados csv da fonte de dados
        ICsvBeanReader beanReader = null;
        CellProcessor[] processors = new CellProcessor[] {
            new NotNull(), //Poster_Link
            new NotNull(), //Series_Title
            new ParseMovieYear(), //Released_Year
            new Optional(), //Certificate
            new ParseMovieIntegerFormat(), //Runtime
            new NotNull(), //Genre
            new ParseDouble(), //IMDB_Rating
            new NotNull(), //Overview
            new Optional(new ParseInt()), //Meta_score
            new NotNull(), //Director
            new NotNull(), //Star1
            new NotNull(), //Star2
            new NotNull(), //Star3
            new NotNull(), //Star4
            new ParseInt(), //No_of_votes
            new Optional(new ParseMovieIntegerFormat()) //Gross
        };

        try {
            //Conexão com o banco MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcurl, usuario, senha);
            connection.setAutoCommit(false);

            //Criação de uma base e tabela para os dados
            Statement st;
            st = connection.createStatement();
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS filmes_db");
            st.executeUpdate("USE filmes_db");
            st.executeUpdate("DROP TABLE IF EXISTS filmes");
            st.executeUpdate("CREATE TABLE filmes (id int NOT NULL AUTO_INCREMENT, nome varchar(100) NOT NULL, ano int NOT NULL, certificado varchar(10) DEFAULT NULL, tempo int NOT NULL, genero varchar(30) NOT NULL, avaliacao_imdb decimal(2,1) NOT NULL, metascore int DEFAULT NULL, diretor varchar(50) NOT NULL, estrela1 varchar(50) NOT NULL, estrela2 varchar(50) NOT NULL, estrela3 varchar(50) NOT NULL, estrela4 varchar(50) NOT NULL, votos int NOT NULL, receita int DEFAULT NULL, PRIMARY KEY (id))");

            //Prepara uma declaração SQL como molde das inserções de dados a serem feitas
            String sql = "INSERT INTO filmes (nome, ano, certificado, tempo, genero, avaliacao_imdb, metascore, diretor, estrela1, estrela2, estrela3, estrela4, votos, receita) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            //Direciona o leitor à fonte de dados
            beanReader = new CsvBeanReader(new FileReader(csvPath), CsvPreference.STANDARD_PREFERENCE);
            beanReader.getHeader(true);

            //Define o cabeçalho dos dados
            String[] header = {"Poster_Link", "Series_Title", "Released_Year", "Certificate", "Runtime", "Genre", "IMDB_Rating", "Overview", "Meta_score", "Director", "Star1", "Star2", "Star3", "Star4", "No_of_Votes", "Gross"};
            Filme bean = null;

            //Contador de declarações em um lote
            int count = 0;

            //Faz a leitura dos dados até o final
            while ((bean = beanReader.read(Filme.class, header, processors)) != null) {
                String titulo = bean.getSeries_Title();
                Integer ano = bean.getReleased_Year();
                String certificado = bean.getCertificate();
                Integer tempo = bean.getRuntime();
                String genero = bean.getGenre();
                Double avaliacaoIMDB = bean.getIMDB_Rating();
                Integer metascore = bean.getMeta_score();
                String diretor = bean.getDirector();
                String estrela1 = bean.getStar1();
                String estrela2 = bean.getStar2();
                String estrela3 = bean.getStar3();
                String estrela4 = bean.getStar4();
                Integer votos = bean.getNo_of_Votes();
                Integer receita = bean.getGross();

                //Insere os dados na declaração SQL
                ps.setString(1, titulo);
                ps.setInt(2, ano);
                if (certificado != null) ps.setString(3, certificado);
                else ps.setNull(3, Types.VARCHAR);
                ps.setInt(4, tempo);
                ps.setString(5, genero);
                ps.setDouble(6, avaliacaoIMDB);
                if (metascore != null) ps.setInt(7, metascore);
                else ps.setNull(7, Types.INTEGER);
                ps.setString(8, diretor);
                ps.setString(9, estrela1);
                ps.setString(10, estrela2);
                ps.setString(11, estrela3);
                ps.setString(12, estrela4);
                ps.setInt(13, votos);
                if (receita != null) ps.setInt(14, receita);
                else ps.setNull(14, Types.INTEGER);

                //Adiciona a declaração no lote
                ps.addBatch();

                //Executa as declarações do lote quando chega a seu limite
                count++;
                if (count % batchSize == 0) {
                    ps.executeBatch();
                    count = 0;
                }
            }
            beanReader.close();

            //Executa o resto das declarações
            ps.executeBatch();

            //Fecha a conexão com o banco
            connection.commit();
            connection.close();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}