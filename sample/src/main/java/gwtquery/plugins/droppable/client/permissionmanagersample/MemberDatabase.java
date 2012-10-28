/*
 * Copyright 2010 Google Inc.
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
package gwtquery.plugins.droppable.client.permissionmanagersample;

import com.google.gwt.user.client.Random;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

import java.util.HashMap;
import java.util.Map;

/**
 * The data source for user
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 */
public class MemberDatabase {

  /**
   * Information about a member.
   */
  public static class MemberInfo implements Comparable<MemberInfo> {

    /**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<MemberInfo> KEY_PROVIDER = new ProvidesKey<MemberInfo>() {
      public Object getKey(MemberInfo item) {
        return item == null ? null : item.getId();
      }
    };

    private static int nextId = 0;

    private String firstName;
    private final int id;
    private String lastName;
    private Permission permission;

    public MemberInfo(Permission permission) {
      this.id = nextId;
      nextId++;
      setPermission(permission);
    }

    public int compareTo(MemberInfo o) {
      return (o == null || o.firstName == null) ? -1 : -o.firstName
          .compareTo(firstName);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof MemberInfo) {
        return id == ((MemberInfo) o).id;
      }
      return false;
    }

    /**
     * @return the member email address
     */
    public String getEmailAddress() {
      return firstName.toLowerCase() + "." + lastName.toLowerCase()
          + "@gmail.com";
    }

    /**
     * @return the member's firstName
     */
    public String getFirstName() {
      return firstName;
    }

    /**
     * @return the member's full name
     */
    public final String getFullName() {
      return firstName + " " + lastName;
    }

    /**
     * @return the unique ID of the member
     */
    public int getId() {
      return this.id;
    }

    /**
     * @return the member's lastName
     */
    public String getLastName() {
      return lastName;
    }

    /**
     * @return the permission of the member
     */
    public Permission getPermission() {
      return permission;
    }

    @Override
    public int hashCode() {
      return id;
    }

    /**
     * Set the member's first name.
     * 
     * @param firstName
     *          the firstName to set
     */
    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    /**
     * Set the member's last name.
     * 
     * @param lastName
     *          the lastName to set
     */
    public void setLastName(String lastName) {
      this.lastName = lastName;
    }

    /**
     * Set the member's permission.
     * 
     * @param category
     *          the category to set
     */
    public void setPermission(Permission permission) {
      this.permission = permission;
    }
  }

  /**
   * A member permission.
   * 
   */
  public static enum Permission {
    OWNER("Owner", 1), COMMITTER("Committer", 5), CONTRIBUTOR("Contributor", 2), NON_MEMBER(
        "Non-Member", 250) ;

    private String displayName;
    private int initialMember;

    private Permission(String displayName, int initialMember) {
      this.displayName = displayName;
      this.initialMember = initialMember;
    }

    public String getDisplayName() {
      return displayName;
    }

    public int getInitialMember() {
      return initialMember;
    }
  }

  private static final String[] FEMALE_FIRST_NAMES = { "Mary", "Patricia",
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
      "Jeanne" };
  /**
   * The singleton instance of the database.
   */
  private static MemberDatabase instance;
  private static final String[] LAST_NAMES = { "Smith", "Johnson", "Williams",
      "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor",
      "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson",
      "Garcia", "Martinez", "Robinson", "Clark", "Rodriguez", "Lewis", "Lee",
      "Walker", "Hall", "Allen", "Young", "Hernandez", "King", "Wright",
      "Lopez", "Hill", "Scott", "Green", "Adams", "Baker", "Gonzalez",
      "Nelson", "Carter", "Mitchell", "Perez", "Roberts", "Turner", "Phillips",
      "Campbell", "Parker", "Evans", "Edwards", "Collins", "Stewart",
      "Sanchez", "Morris", "Rogers", "Reed", "Cook", "Morgan", "Bell",
      "Murphy", "Bailey", "Rivera", "Cooper", "Richardson", "Cox", "Howard",
      "Ward", "Torres", "Peterson", "Gray", "Ramirez", "James", "Watson",
      "Brooks", "Kelly", "Sanders", "Price", "Bennett", "Wood", "Barnes",
      "Ross", "Henderson", "Coleman", "Jenkins", "Perry", "Powell", "Long",
      "Patterson", "Hughes", "Flores", "Washington", "Butler", "Simmons",
      "Foster", "Gonzales", "Bryant", "Alexander", "Russell", "Griffin",
      "Diaz", "Hayes", "Myers", "Ford", "Hamilton", "Graham", "Sullivan",
      "Wallace", "Woods", "Cole", "West", "Jordan", "Owens", "Reynolds",
      "Fisher", "Ellis", "Harrison", "Gibson", "Mcdonald", "Cruz", "Marshall",
      "Ortiz", "Gomez", "Murray", "Freeman", "Wells", "Webb", "Simpson",
      "Stevens", "Tucker", "Porter", "Hunter", "Hicks", "Crawford", "Henry",
      "Boyd", "Mason", "Morales", "Kennedy", "Warren", "Dixon", "Ramos",
      "Reyes", "Burns", "Gordon", "Shaw", "Holmes", "Rice", "Robertson",
      "Hunt", "Black", "Daniels", "Palmer", "Mills", "Nichols", "Grant",
      "Knight", "Ferguson", "Rose", "Stone", "Hawkins", "Dunn", "Perkins",
      "Hudson", "Spencer", "Gardner", "Stephens", "Payne", "Pierce", "Berry",
      "Matthews", "Arnold", "Wagner", "Willis", "Ray", "Watkins", "Olson",
      "Carroll", "Duncan", "Snyder", "Hart", "Cunningham", "Bradley", "Lane",
      "Andrews", "Ruiz", "Harper", "Fox", "Riley", "Armstrong", "Carpenter",
      "Weaver", "Greene", "Lawrence", "Elliott", "Chavez", "Sims", "Austin",
      "Peters", "Kelley", "Franklin", "Lawson" };

  private static final String[] MALE_FIRST_NAMES = { "James", "John", "Robert",
      "Michael", "William", "David", "Richard", "Charles", "Joseph", "Thomas",
      "Christopher", "Daniel", "Paul", "Mark", "Donald", "George", "Kenneth",
      "Steven", "Edward", "Brian", "Ronald", "Anthony", "Kevin", "Jason",
      "Matthew", "Gary", "Timothy", "Jose", "Larry", "Jeffrey", "Frank",
      "Scott", "Eric", "Stephen", "Andrew", "Raymond", "Gregory", "Joshua",
      "Jerry", "Dennis", "Walter", "Patrick", "Peter", "Harold", "Douglas",
      "Henry", "Carl", "Arthur", "Ryan", "Roger", "Joe", "Juan", "Jack",
      "Albert", "Jonathan", "Justin", "Terry", "Gerald", "Keith", "Samuel",
      "Willie", "Ralph", "Lawrence", "Nicholas", "Roy", "Benjamin", "Bruce",
      "Brandon", "Adam", "Harry", "Fred", "Wayne", "Billy", "Steve", "Louis",
      "Jeremy", "Aaron", "Randy", "Howard", "Eugene", "Carlos", "Russell",
      "Bobby", "Victor", "Martin", "Ernest", "Phillip", "Todd", "Jesse",
      "Craig", "Alan", "Shawn", "Clarence", "Sean", "Philip", "Chris",
      "Johnny", "Earl", "Jimmy", "Antonio", "Danny", "Bryan", "Tony", "Luis",
      "Mike", "Stanley", "Leonard", "Nathan", "Dale", "Manuel", "Rodney",
      "Curtis", "Norman", "Allen", "Marvin", "Vincent", "Glenn", "Jeffery",
      "Travis", "Jeff", "Chad", "Jacob", "Lee", "Melvin", "Alfred", "Kyle",
      "Francis", "Bradley", "Jesus", "Herbert", "Frederick", "Ray", "Joel",
      "Edwin", "Don", "Eddie", "Ricky", "Troy", "Randall", "Barry",
      "Alexander", "Bernard", "Mario", "Leroy", "Francisco", "Marcus",
      "Micheal", "Theodore", "Clifford", "Miguel", "Oscar", "Jay", "Jim",
      "Tom", "Calvin", "Alex", "Jon", "Ronnie", "Bill", "Lloyd", "Tommy",
      "Leon", "Derek", "Warren", "Darrell", "Jerome", "Floyd", "Leo", "Alvin",
      "Tim", "Wesley", "Gordon", "Dean", "Greg", "Jorge", "Dustin", "Pedro",
      "Derrick", "Dan", "Lewis", "Zachary", "Corey", "Herman", "Maurice",
      "Vernon", "Roberto", "Clyde", "Glen", "Hector", "Shane", "Ricardo",
      "Sam", "Rick", "Lester", "Brent", "Ramon", "Charlie", "Tyler", "Gilbert",
      "Gene" };

  /**
   * Get the singleton instance of the member database.
   * 
   * @return the singleton instance
   */
  public static MemberDatabase get() {
    if (instance == null) {
      instance = new MemberDatabase();
    }
    return instance;
  }

  /**
   * The provider that holds the list of contacts in the database.
   */
  private Map<Permission, ListDataProvider<MemberInfo>> projectMembersByPermission = new HashMap<Permission, ListDataProvider<MemberInfo>>();

  /**
   * Construct a new contact database.
   */
  private MemberDatabase() {

    // init data providers list
    for (Permission p : Permission.values()) {
      projectMembersByPermission.put(p, generateContacts(p));
    }
  }

  /**
   * Add a display to the database. The current range of interest of the display
   * will be populated with data.
   * 
   * @param display
   *          a {@Link HasData}.
   */
  public void addDataDisplay(HasData<MemberInfo> display,
      Permission selectedPermission) {
    getDataProvider(selectedPermission).addDataDisplay(display);
  }

  /**
   * Generate the member for a specified permissions
   * 
   */
  public ListDataProvider<MemberInfo> generateContacts(Permission p) {
    ListDataProvider<MemberInfo> list = new ListDataProvider<MemberInfo>();
    for (int i = 0; i < p.getInitialMember(); i++) {
      MemberInfo member = createMemberInfo(p);
      // avoid duplicate
      list.getList().remove(member);
      list.getList().add(member);
    }
    return list;
  }

  public ListDataProvider<MemberInfo> getDataProvider(Permission p) {
    return projectMembersByPermission.get(p);
  }

  /**
   * method called when we change the permission on a member
   * 
   * @param contact
   *          the contact to add.
   */
  public void permissionChange(MemberInfo member, Permission newPermission) {
    if (newPermission == member.getPermission()) {
      return;
    }

    ListDataProvider<MemberInfo> previousList = getDataProvider(member
        .getPermission());
    ListDataProvider<MemberInfo> newList = getDataProvider(newPermission);
    previousList.getList().remove(member);
    newList.getList().add(member);
    member.setPermission(newPermission);

    previousList.refresh();
    newList.refresh();
  }

  /**
   * Get the categories in the database.
   * 
   * @return the categories in the database
   */
  public Permission[] queryPermissions() {
    return Permission.values();
  }

  /**
   * Create a new random {@link MemberInfo}.
   * 
   * @param p
   * 
   * @return the new {@link MemberInfo}.
   */
  private MemberInfo createMemberInfo(Permission p) {

    MemberInfo member = new MemberInfo(p);
    member.setLastName(nextValue(LAST_NAMES));
    if (Random.nextBoolean()) {
      // Male.
      member.setFirstName(nextValue(MALE_FIRST_NAMES));
    } else {
      // Female.
      member.setFirstName(nextValue(FEMALE_FIRST_NAMES));
    }

    return member;
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
