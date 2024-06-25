package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    private final DataSource dataSource;

    @Autowired
    public MySqlCategoryDao(DataSource dataSource, DataSource dataSource1) {
        super(dataSource);
        this.dataSource = dataSource1;
    }



    @Override
    public List<Category> getAllCategories()
    {
        List<Category> categories = new ArrayList<>();
        String query = " SELECT * FROM categories";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()){

            while (resultSet.next()){
                int categoryId = resultSet.getInt("category_id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                Category category = new Category(categoryId,name,description);
                categories.add(category);
            }



        }catch (Exception ex){
            ex.printStackTrace();
        }

        // get all categories
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        String query = "SELECT * FROM categories WHERE category_id = ?";
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){

                preparedStatement.setInt(1,categoryId);
                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    if(resultSet.next()){
                        int categoryIdSql = resultSet.getInt("category_id");
                        String name = resultSet.getString("name");
                        String description = resultSet.getString("description");
                        Category category = new Category(categoryIdSql,name,description);
                       return category;
                    }
                }


        }catch (Exception ex){
            ex.printStackTrace();
        }
        // get category by id
        return null;
    }

    @Override
    public Category create(Category category)
    {
        String query = "INSERT INTO categories (name) VALUES(?)";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, category.getName());

            int affectedRows = preparedStatement.executeUpdate();

            if(affectedRows == 0){
                throw new SQLException("Creating category failed");
            }

            try(ResultSet generatedKeys = preparedStatement.getGeneratedKeys()){
                if(generatedKeys.next()){
                    int generatedId = generatedKeys.getInt(1);
                    category.setCategoryId(generatedId);
                }else{
                    throw new SQLException("Creating category failed");
                }
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

        // create a new category
        return category;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        String query = "UPDATE categories SET name = ? WHERE category_id = ?";
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1,category.getName());
            preparedStatement.setInt(2, categoryId);

            preparedStatement.executeUpdate();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        // update category
    }

    @Override
    public void delete(int categoryId)
    {
        String query = "DELETE FROM categories WHERE category_id = ?";

        try(Connection connection = dataSource.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(query)){

                preparedStatement.setInt(1,categoryId);
                preparedStatement.executeUpdate();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        // delete category
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
