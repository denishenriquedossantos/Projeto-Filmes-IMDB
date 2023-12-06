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
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        //Link utilizado na conexão do banco de dados
        String jdbcurl = "jdbc:mysql://localhost:3306/?zeroDateTimeBehavior=convertToNull";
        //Nome do usuário e senha para acessar o banco de dados
        String usuario = "root";
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

            //Criação de uma base e tabelas para os dados
            Statement st;
            st = connection.createStatement();
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS filmes_db");
            st.executeUpdate("USE filmes_db");
            st.executeUpdate("DROP TABLE IF EXISTS generos");
            st.executeUpdate("DROP TABLE IF EXISTS estrelas");
            st.executeUpdate("DROP TABLE IF EXISTS filmes");
            st.executeUpdate("CREATE TABLE filmes (nome varchar(100) NOT NULL, ano int NOT NULL, certificado varchar(10) DEFAULT NULL, tempo int NOT NULL, avaliacao_imdb decimal(2,1) NOT NULL, metascore int DEFAULT NULL, diretor varchar(50) NOT NULL, votos int NOT NULL, receita int DEFAULT NULL, PRIMARY KEY (nome))");
            st.executeUpdate("CREATE TABLE generos (nome varchar(100) NOT NULL, genero varchar(30) NOT NULL, PRIMARY KEY (nome, genero), FOREIGN KEY (nome) REFERENCES filmes(nome) ON DELETE CASCADE)");
            st.executeUpdate("CREATE TABLE estrelas (nome varchar(100) NOT NULL, estrela varchar(50) NOT NULL, PRIMARY KEY (nome, estrela), FOREIGN KEY (nome) REFERENCES filmes(nome) ON DELETE CASCADE)");

            //Prepara declarações SQL como moldes das inserções de dados a serem feitas
            String sql = "INSERT INTO filmes (nome, ano, certificado, tempo, avaliacao_imdb, metascore, diretor, votos, receita) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps_filmes = connection.prepareStatement(sql);
            sql = "INSERT INTO generos (nome, genero) VALUES (?, ?)";
            PreparedStatement ps_generos = connection.prepareStatement(sql);
            sql = "INSERT INTO estrelas (nome, estrela) VALUES (?, ?)";
            PreparedStatement ps_estrelas = connection.prepareStatement(sql);

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
                String[] genero = bean.getGenre().split("[, ]+");
                Double avaliacaoIMDB = bean.getIMDB_Rating();
                Integer metascore = bean.getMeta_score();
                String diretor = bean.getDirector();
                Set<String> estrela = new HashSet<String>();
                estrela.add(bean.getStar1());
                estrela.add(bean.getStar2());
                estrela.add(bean.getStar3());
                estrela.add(bean.getStar4());
                Integer votos = bean.getNo_of_Votes();
                Integer receita = bean.getGross();

                //Corrigindo caso duplicado
                if (titulo.equals("Drishyam")) {
                    titulo = titulo + " (" + ano + ")";
                }

                //Insere os dados nas declarações SQL
                ps_filmes.setString(1, titulo);
                ps_filmes.setInt(2, ano);
                if (certificado != null) ps_filmes.setString(3, certificado);
                else ps_filmes.setNull(3, Types.VARCHAR);
                ps_filmes.setInt(4, tempo);
                ps_filmes.setDouble(5, avaliacaoIMDB);
                if (metascore != null) ps_filmes.setInt(6, metascore);
                else ps_filmes.setNull(6, Types.INTEGER);
                ps_filmes.setString(7, diretor);
                ps_filmes.setInt(8, votos);
                if (receita != null) ps_filmes.setInt(9, receita);
                else ps_filmes.setNull(9, Types.INTEGER);

                //Adiciona as declarações no lote
                ps_filmes.addBatch();
                for (String s : genero) {
                    ps_generos.setString(1, titulo);
                    ps_generos.setString(2, s);
                    ps_generos.addBatch();
                }
                for (String s : estrela) {
                    ps_estrelas.setString(1, titulo);
                    ps_estrelas.setString(2, s);
                    ps_estrelas.addBatch();
                }

                //Executa as declarações do lote quando chega a seu limite
                count++;
                if (count % batchSize == 0) {
                    ps_filmes.executeBatch();
                    ps_generos.executeBatch();
                    ps_estrelas.executeBatch();
                    count = 0;
                }
            }
            beanReader.close();

            //Executa o resto das declarações
            ps_filmes.executeBatch();
            ps_generos.executeBatch();
            ps_estrelas.executeBatch();

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