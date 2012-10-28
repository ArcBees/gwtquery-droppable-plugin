/*
 * Copyright 2010 The gwtquery plugins team.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package gwtquery.plugins.droppable.client.gfindersample;

import com.google.gwt.user.client.Random;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class simulating the file system.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class FileSystem {

  /**
   * File object
   */
  public static class File implements Comparable<File> {

    /**
     * The key provider that provides the unique ID of a file.
     */
    public static final ProvidesKey<File> KEY_PROVIDER = new ProvidesKey<File>() {
      public Object getKey(File item) {
        return item == null ? null : item.getId();
      }
    };

    private static int nextId = 0;

    private final int id;
    private String name;
    private File parent;
    private FileType type;

    public File(FileType type) {
      this.type = type;
      this.id = nextId;
      nextId++;
    }

    public int compareTo(File o) {
      if (o == null) {
        return -1;
      }
      if (o.getType() != type) {
        return (type == FileType.DIRECTORY) ? 1 : -1;
      }
      return (o.name == null) ? -1 : -o.name.compareTo(name);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof File) {
        return id == ((File) o).id;
      }
      return false;
    }

    /**
     * @return the unique ID of the file
     */
    public int getId() {
      return this.id;
    }

    /**
     * @return the file's address
     */
    public String getName() {
      return name;
    }

    /**
     * @return the parent directory or null if it is root
     */
    public File getParent() {
      return parent;
    }

    /**
     * @return the type of the file
     */
    public FileType getType() {
      return type;
    }

    @Override
    public int hashCode() {
      return id;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setParent(File parent) {
      this.parent = parent;
    }

  }

  /**
   * File type
   */
  public static enum FileType {
    DIRECTORY, FILE;
  }

  /**
   * The singleton instance.
   */
  private static FileSystem instance;

  private static int MAX_DIR_NBR = 10;
  private static int MAX_FILE_NBR = 15;
  private static final String ROOT_DIRECTORY = "Pictures";

  private static final String[] USER_DIRECTORY_NAME = { "Mary", "Patricia",
      "Linda", "Barbara", "Elizabeth", "Jennifer", "Maria", "Susan",
      "Margaret", "Dorothy", "Lisa", "Nancy", "Karen", "Betty", "Helen",
      "Sandra", "Donna", "Carol", "Ruth", "Sharon", "Michelle", "Laura",
      "Sarah", "Kimberly", "Deborah", "Jessica", "Shirley", "Cynthia",
      "Angela", "Melissa", "Brenda", "Amy", "Anna", "Rebecca", "Virginia",
      "Kathleen", "Pamela", "Martha", "Debra", "Amanda", "Stephanie",
      "Carolyn", "Christine", "Marie", "Janet", "Catherine", "Frances", "Ann",
      "Joyce", "Diane", "Alice", "Julie", "Heather", "Teresa", "Doris",
      "Gloria", "Evelyn", "Jean", "Cheryl", "Mildred", "Katherine", "Joan",
      "Ashley", "Judith", "Rose", "Janice", "Kelly", "Nicole", "Judy",
      "Christina", "Kathy", "Theresa", "Beverly", "Denise", "Tammy", "Irene",
      "Jane", "Lori", "Rachel", "Marilyn", "Andrea", "Kathryn", "Louise",
      "Sara", "Anne", "Jacqueline", "Wanda", "Bonnie", "Julia", "Ruby", "Lois",
      "Tina", "Phyllis", "Norma", "Paula", "Diana", "Annie", "Lillian",
      "Emily", "Robin", "Peggy", "Crystal", "Gladys", "Rita", "Dawn", "Connie",
      "Florence", "Tracy", "Edna", "Tiffany", "Carmen", "Rosa", "Cindy",
      "Grace", "Wendy", "Victoria", "Edith", "Kim", "Sherry", "Sylvia",
      "Josephine", "Thelma", "Shannon", "Sheila", "Ethel", "Ellen", "Elaine",
      "Marjorie", "Carrie", "Charlotte", "Monica", "Esther", "Pauline", "Emma",
      "Juanita", "Anita", "Rhonda", "Hazel", "Amber", "Eva", "Debbie", "April",
      "Leslie", "Clara", "Lucille", "Jamie", "Joanne", "Eleanor", "Valerie",
      "Danielle", "Megan", "Alicia", "Suzanne", "Michele", "Gail", "Bertha",
      "Darlene", "Veronica", "Jill", "Erin", "Geraldine", "Lauren", "Cathy",
      "Joann", "Lorraine", "Lynn", "Sally", "Regina", "Erica", "Beatrice",
      "Dolores", "Bernice", "Audrey", "Yvonne", "Annette", "June", "Samantha",
      "Marion", "Dana", "Stacy", "Ana", "Renee", "Ida", "Vivian", "Roberta",
      "Holly", "Brittany", "Melanie", "Loretta", "Yolanda", "Jeanette",
      "Laurie", "Katie", "Kristen", "Vanessa", "Alma", "Sue", "Elsie", "Beth",
      "Jeanne", "James", "John", "Robert", "Michael", "William", "David",
      "Richard", "Charles", "Joseph", "Thomas", "Christopher", "Daniel",
      "Paul", "Mark", "Donald", "George", "Kenneth", "Steven", "Edward",
      "Brian", "Ronald", "Anthony", "Kevin", "Jason", "Matthew", "Gary",
      "Timothy", "Jose", "Larry", "Jeffrey", "Frank", "Scott", "Eric",
      "Stephen", "Andrew", "Raymond", "Gregory", "Joshua", "Jerry", "Dennis",
      "Walter", "Patrick", "Peter", "Harold", "Douglas", "Henry", "Carl",
      "Arthur", "Ryan", "Roger", "Joe", "Juan", "Jack", "Albert", "Jonathan",
      "Justin", "Terry", "Gerald", "Keith", "Samuel", "Willie", "Ralph",
      "Lawrence", "Nicholas", "Roy", "Benjamin", "Bruce", "Brandon", "Adam",
      "Harry", "Fred", "Wayne", "Billy", "Steve", "Louis", "Jeremy", "Aaron",
      "Randy", "Howard", "Eugene", "Carlos", "Russell", "Bobby", "Victor",
      "Martin", "Ernest", "Phillip", "Todd", "Jesse", "Craig", "Alan", "Shawn",
      "Clarence", "Sean", "Philip", "Chris", "Johnny", "Earl", "Jimmy",
      "Antonio", "Danny", "Bryan", "Tony", "Luis", "Mike", "Stanley",
      "Leonard", "Nathan", "Dale", "Manuel", "Rodney", "Curtis", "Norman",
      "Allen", "Marvin", "Vincent", "Glenn", "Jeffery", "Travis", "Jeff",
      "Chad", "Jacob", "Lee", "Melvin", "Alfred", "Kyle", "Francis", "Bradley",
      "Jesus", "Herbert", "Frederick", "Ray", "Joel", "Edwin", "Don", "Eddie",
      "Ricky", "Troy", "Randall", "Barry", "Alexander", "Bernard", "Mario",
      "Leroy", "Francisco", "Marcus", "Micheal", "Theodore", "Clifford",
      "Miguel", "Oscar", "Jay", "Jim", "Tom", "Calvin", "Alex", "Jon",
      "Ronnie", "Bill", "Lloyd", "Tommy", "Leon", "Derek", "Warren", "Darrell",
      "Jerome", "Floyd", "Leo", "Alvin", "Tim", "Wesley", "Gordon", "Dean",
      "Greg", "Jorge", "Dustin", "Pedro", "Derrick", "Dan", "Lewis", "Zachary",
      "Corey", "Herman", "Maurice", "Vernon", "Roberto", "Clyde", "Glen",
      "Hector", "Shane", "Ricardo", "Sam", "Rick", "Lester", "Brent", "Ramon",
      "Charlie", "Tyler", "Gilbert", "Gene" };

  /**
   * Get the singleton instance.
   * 
   * @return the singleton instance
   */
  public static FileSystem get() {
    if (instance == null) {
      instance = new FileSystem();
    }
    return instance;
  }

  /**
   * The provider that holds the list of files in a directory.
   */
  private Map<File, ListDataProvider<File>> filesByDirectory = new HashMap<File, ListDataProvider<File>>();
  private ListDataProvider<File> rootDirectories = new ListDataProvider<File>();

  /**
   * Construct the file system.
   */
  private FileSystem() {

    File rootDirectory = new File(FileType.DIRECTORY);
    rootDirectory.setName(ROOT_DIRECTORY);

    generateDirTree(rootDirectory);

    rootDirectories.getList().add(rootDirectory);

  }

  /**
   * Return the RootDirectories
   * @return
   */
  public ListDataProvider<File> getRootDirectories() {
    return rootDirectories;

  }

  /**
   * List the files in the directory <code>dir</code>
   * 
   * @param dir
   * 
   */
  public ListDataProvider<File> ls(File dir) {
    return filesByDirectory.get(dir);
  }

  /**
   * Move the file <code>file</code> to the directory <code>destination</code>
   * 
   */
  public void moveFile(File file, File destination) {
    assert destination.getType() == FileType.DIRECTORY;

    File oldDestination = file.getParent();
    List<File> previousList = ls(oldDestination).getList();
    List<File> newList = ls(destination).getList();

    previousList.remove(file);
    newList.add(file);

    file.setParent(destination);
  }

  /**
   * Generate contents for the directory <code>root</code>
   * 
   * @param root
   * @param fileNbr
   * @param subDirNbr
   */
  private void generateDirContent(File root, int fileNbr, int subDirNbr) {

    assert root.getType() == FileType.DIRECTORY;

    ListDataProvider<File> files = new ListDataProvider<File>();

    for (int i = 0; i < subDirNbr; i++) {
      File f = new File(FileType.DIRECTORY);
      f.setName(root.getName() + "Event" + i);
      f.setParent(root);
      files.getList().add(f);

      generateDirContent(f, Math.max(5, Random.nextInt(MAX_FILE_NBR)), 0);
    }

    for (int i = 0; i < fileNbr; i++) {
      File f = new File(FileType.FILE);
      f.setName(root.getName() + "PICT" + i + ".jpg");
      f.setParent(root);
      files.getList().add(f);
    }

    filesByDirectory.put(root, files);

  }

  /**
   * Generate the file system
   * 
   * @param rootDirectory
   */
  private void generateDirTree(File rootDirectory) {
    // generate user directories
    ListDataProvider<File> files = new ListDataProvider<File>();

    for (int i = 0; i < Math.max(10, Random.nextInt(20)); i++) {
      File f = new File(FileType.DIRECTORY);
      f.setName(nextValue(USER_DIRECTORY_NAME));
      f.setParent(rootDirectory);
      files.getList().add(f);
      // generate Event sub directories
      generateDirContent(f, 0, Math.max(5, Random.nextInt(MAX_DIR_NBR)));

    }
    filesByDirectory.put(rootDirectory, files);

  }

  /**
   * Get the next random value from an array.
   * 
   * @param array
   *          the array
   * @return a random value in the array
   */
  private <T> T nextValue(T[] array) {
    return array[Random.nextInt(array.length)];
  }

}
