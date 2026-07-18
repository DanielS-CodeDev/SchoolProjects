
"""
File: street.py
Author: Daniel S
Course: CSC 120, Spring 2025
Purpose: This program prompts the user to input a single letter either, p for
    a park, e for an empty lot and/ or b for a building. For each letter there
    are different input configurations for telling the program how to draw the
    ASCII art image. Each input for a building, park and empty lot is separted
    by a space.
"""
class Park:
    """
    This class creates the Park for the ASCII image. 

    This class has methods that help create the ASCII art, get the height of
    the default ASCII art image and a method that gives just one line of the
    ASCII art of the park. 
    """
    def __init__(self, width, foliage):
        """
        This is the constructor of the class that sets up the width and
        foliage attributes of the class.
  
        Parameters: 'self', instance of the class. 'width', integer used to
            determine the width of the park. 'foliage', string that is used
            to change the leaves on the tree.
  
        Returns: None.
        """
        self._width = width
        self._foliage = foliage

    def create(self, height):
        """
        Creates the ASCII art of the park, calls the 'park_builder' function
        to help create the park image.
  
        Parameters: 'self', instance of the class. 'height', integer, that is
            the maximum height of the ASCII image used to add whitespace above
            if needed.
  
        Returns: A list of strings that is the ASCII art.
        """
        tree_list = park_builder(height, self._width)
        return replace(self._foliage, "*", tree_list)

    def at_height(self, line, height):
        """
        Returns one line of the '.create()' methods ASCII art.

        Paramters: 'self', instance of the class. 'height', integer, that is
            the maximum height of the ASCII image used for the 'create'
            method. 'line' is an integer that is the line that is returned
            form the ASCII art list.

        Returns: A string, a single line of the ASCII art.
        """
        return self.create(height)[line]
    
    def get_default_height(self):
        """
        Returns the default height of the Park which is always 6.

        Paramters: 'self', instance of the class.

        Returns: An integer that is the default height of the park.
        """
        return 6
    
class Building:
    """
    This class creates the Building for the ASCII image. 

    This class has methods that help create the ASCII art, get the height of
    the default ASCII art image and a method that gives just one line of the
    ASCII art of the building. 
    """
    def __init__(self, width, height, brick):
        """
        This is the constructor of the class that sets up the width and height
        of the building along with the character type for the brick.
  
        Parameters: 'self', instance of the class. 'width', integer used to
            determine the width of the building. 'height', integer that is
            used to determine the height of the buidling. 'brick', string used
            for the brick image of the building.
  
        Returns: None.
        """
        self._width = width
        self._height = height
        self._brick = brick

    def create(self, total_height):
        """
        Creates the ASCII art of the building. Uses 'create_default' method 
        and the 'extend_image_height' function to create the ASCII art.
  
        Parameters: 'self', instance of the class. 'total_height', integer,
            that is the maximum height of the ASCII image used to add
            whitespace above if needed.
  
        Returns: A list of strings that is the ASCII art.
        """
        default_building = self.create_default()
        extra_height = total_height - len(default_building)
        return extend_image_height(self._width, default_building, 
                                      extra_height)

    def create_default(self):
        """
        Creates the default ASCII art of the building. It calls the
        'create_default_building' function to create the ASCII art.
  
        Parameters: 'self', instance of the class.
  
        Returns: A list of strings that is the ASCII art.
        """
        return [" " * self._width] + \
            create_default_building(self._width, self._height, self._brick)
    
    def get_default_height(self):
        """
        Returns the default height of the Park before the extra whitespace is
        added above. +1 because one extra whitespace above is the default.

        Paramters: 'self', instance of the class.

        Returns: An integer that is the default height of the building.
        """
        return self._height + 1
    
    def at_height(self, line, total_height):
        """
        Returns one line of the '.create()' methods ASCII art.

        Paramters: 'self', instance of the class. 'total_height', integer,
            that is the maximum height of the ASCII image used for the
            'create' method. 'line' is an integer that is the line that is
            returned form the ASCII art list.

        Returns: A string, a single line of the ASCII art.
        """
        return self.create(total_height)[line]
    
class EmptyLot:
    """
    This class creates the Empty Lot for the ASCII image. 

    This class has methods that help create the ASCII art, get the height of
    the default ASCII art image and a method that gives just one line of the
    ASCII art of the empty lot. 
    """
    def __init__(self, width, trash):
        """
        This is the constructor of the class that sets up the width of the
        empty lot and the trash string for the empty lot.
  
        Parameters: 'self', instance of the class. 'width', integer used to
            determine the width of the empty lot. 'trash', string used
            to put into the empty lot.
  
        Returns: None.
        """
        self._width = width
        self._trash = trash

    def get_default_height(self):
        """
        Returns the default height of the empty lot which is always 2.

        Paramters: 'self', instance of the class.

        Returns: An integer that is the default height of the building.
        """
        return 2
    
    def create_default(self):
        """
        Creates the default ASCII art of the empty lot. It calls the
        'create_default_lot' and 'replace' functions to create the empty lot.
  
        Parameters: 'self', instance of the class.
  
        Returns: A list of strings that is the ASCII art.
        """
        # index at zero because it is just one string, replace takes a list
        trash = replace(" ", "_", [self._trash])[0] 
        return [" " * self._width] + [create_default_lot(self._width, trash)]
    
    def create(self, total_height):
        """
        Creates the ASCII art of the empty lot. Uses 'create_default' method 
        and the 'extend_image_height' function to create the ASCII art.
  
        Parameters: 'self', instance of the class. 'total_height', integer,
            that is the maximum height of the ASCII image used to add
            whitespace above if needed.
  
        Returns: A list of strings that is the ASCII art.
        """
        # 2 is the default height
        extra_height = total_height - 2
        default_empty_lot = self.create_default()
        return extend_image_height(self._width, 
                                   default_empty_lot, extra_height)
    
    def at_height(self, line, total_height):
        """
        Returns one line of the '.create()' methods ASCII art.

        Paramters: 'self', instance of the class. 'total_height', integer,
            that is the maximum height of the ASCII image used for the
            'create' method. 'line' is an integer that is the line that is
            returned form the ASCII art list.

        Returns: A string, a single line of the ASCII art.
        """
        return self.create(total_height)[line]

def create_default_lot(width, trash):
    """
    Creates the empty lot with the trash string spread throughout the width if
    the trash string can not fit evenly through it is cut to fit.

    Paramters: 'width', integer used in the math for determining how much
        trash can fit within the width. 'trash' string used to be put into the
        width of the empty lot.

    Returns: A string, the with of the empty lot with the trash along the
        width of the empty lot.
    """
    multiplier = width// len(trash)
    new_string = trash * multiplier

    add_on = ""
    if len(new_string) % width != 0:
        add_on = trash[:width - len(new_string) % width]
    
    return new_string + add_on

def extend_image_height(width, alist, extra_height):
    """
    Used to extend the ASCII art height to match the maximum ASCII arts height
    within the image. Filled with extra whitespace with the same width.

    Parameters: 'width', integer used to have the same width of extra 
        whitespace above the ASCII art section. 'alist' a list of each line
        of the ASCII art image used to append the extra whitespace too.
        'extra_height' is used to determine how much extra whitespace is
        needed to be added to the image.
    """
    if extra_height == 0:
        return alist
    
    else:
        return [" " * width] + \
            extend_image_height(width, alist, extra_height - 1)

def create_default_building(width, height, brick):
    """
    Creates the default building ASCII art list of lines based on the width
    and height parameters.

    Parameters: 'width', integer used to know how long the building is/ the 
        length of each element. 'height', integer used to know how tall the
        building is/ how many elements are needed. 'brick', string the image
        texture of the building used as the characters of the elements within
        the list.

    Returns: A list, each element is a line in the ASCII art of the
        building. The number of elements is the height of the buidling.
    """
    if height == 0:
        return []
    
    else:
        return [brick * width] + \
            create_default_building(width, height - 1, brick)

def park_builder(height, new_width):
    """
    Creates the park ASCII art list. Calls helper functions to extend the
    default length and height.

    Paramters: 'height' integer used to determine how much extra whitespace is
        need to add on top of the park. 'new_width', integer used to determine
        how much extra with is need to add to the sides of the default park.

    Returns: A list, of the ASCII art of the park. Each element is a level
        of the ASCII art.
    """
    # Default Park/ ASCII tree list
    tree_list = ["     ", "  *  ", " *** ", "*****", "  |  ", "  |  "]

    # helper - width extending - finds the amount needed to extend by
    total_extra_width = new_width - 5
    bit_extra_width = int((total_extra_width)/ 2)
    tree_list = park_extend_width(tree_list, bit_extra_width)

    # helper - height extending - match other street heights
    extra_height = height - 6
    tree_list = extend_image_height(new_width, tree_list, extra_height)

    return tree_list

def park_extend_width(tree_list, bit_extra_width):
    """
    Helper function for 'park_builder'. Extends the width of each side of the
    park to fit the inputed number width. The default width is 5.

    Parameters: 'tree_list', list that is the ASCII art for the park, used to
        help add on the extra width whitespace. 'bit_extra_width', integer
        used to determine the amount of extra width whitespace needed.

    Returns: A list of the updated ASCII art with the extra whitespace width.
    """
    if tree_list == []:
        return []
    
    else: 
        return [" " * bit_extra_width + tree_list[0] + \
                 " " * bit_extra_width] + \
                park_extend_width(tree_list[1:], bit_extra_width)

def replace(new, old, alist):
    """
    Has a helper function, replaces characters within a list of strings.

    Parameters: 'new', string that replaces the old string, used in the helper
        fucntion call. 'old', string that is replaced with the new string,
        used in the helper fucntion call. 'alist' a list of strings that is
        iterated over, used in the helper function call.

    Returns: A list of strings with the new string that replaced the old
        string.
    """
    # The outside looper for going through the list
    if alist == []:
        
        return []

    else:
        return [replace_helper(new, old, alist[0])] + \
            replace(new, old, alist[1:])

def replace_helper(new, old, string):
    """
    The helper function for replace that loops over the individual string and
    replaces the old string with the new string.

    Parameters: 'new', string that replaces the old string. 'old', string that
        is replaced with the new string. 'alist' a list of strings that is
        iterated over.
    
    Returns: A list of strings with the new string that replaced the old
        string.
    """
    if string == "":
        return string

    else:
        if string[0] == old:
            return new + replace_helper(new, old, string[1:])
        else:
            return string[0] + replace_helper(new, old, string[1:])

def display(slist):
    """
    Has a helper function. Displays the ASCII art line by line.

    Parameters: 'slist' a list of the ASCII art. The elements are each line of
        the ASCII art, used in a conditional and a parameter.

    Returns: None.
    """
    if len(slist) != 0:
        print("+" + "-" * len(slist[0]) + "+")
        display_helper(slist)
        print("+" + "-" * len(slist[0]) + "+")

def display_helper(slist):
    """
    The helper function for 'display', prints the lines of the ASCII art with
    a boarder.

    Parameters: 'slist' a list of the ASCII art which is sliced and even
        indexed.
    """
    if slist == []:
        return ""
    else:
        print("|" + slist[0] + "|")
        display_helper(slist[1:])

def recur_max(alist):
    """
    Gets the maximum number in a list and returns the max value.

    Parameters: 'alist' a list of numbers that is iterated through and is used
        to find the max value.

    Returns: An integer that is the max in the alist.
    """
    if alist == []:
        return 0

    else:
        return max(alist[0], recur_max(alist[1:]))

def get_index_split(string, spliton, index):
    """
    Helper function for 'recur_split', gets the indexs for the split and puts
    them into a list.

    Parameters: 'string', string that is sliced to find the indexs needed to
        split on. 'spliton' is the character that is used in a conditional
        and paramter and is used to determine the character that needs to be
        split on. 'index', integer used to helped determine the indexes that
        need to be split on.

    Returns: A list of integers that are the indexes the string needs to be
        split on.
    """
    if string == "":
        return []
    else:
        if string[0] == spliton:
            return [index] + get_index_split(string[1:], spliton, index + 1)

        else:
            return get_index_split(string[1:], spliton, index + 1)

def split_on_index(string, index_list, spliton):
    """
    Helper function for 'recur_split'. It splits the string on the indexes
    of the spliton character.

    Parameters: 'string', string that is sliced to find the indexs needed to
        split on. 'spliton' is the character that is used in a conditional
        and paramter and is used to determine the character that needs to be
        split on. 'index_list' is the list of indexes that are the indexes
        that the string is split on.

    Returns: A list of the string sliced up at the indexes.
    """
    if len(index_list) == 1:
        return []

    else:
        # If index is on spliton and we start split on it, then += 1
        if string[index_list[0]] == spliton:
            index_list[0] += 1

        return [string[index_list[0]:index_list[1]]] + \
            split_on_index(string, index_list[1:], spliton)

def recur_split(string, spliton):
    """
    Has helper functions. It calls the helper fucntions to split a string
    on a character, spliton.

    Parameters: 'string', string that is split up on, used in function
        parameters. 'spilton', string used to determine where to split the
        string at and is used as a parameter.

    Returns: A list of the string that is broken up by the split on string.
    """
    split_index_list = get_index_split(string, spliton, 0)

    # Add starting and ending slicing for the other function
    split_index_list = [0] + split_index_list + [len(string)]

    return split_on_index(string, split_index_list, spliton)

def get_all_max_height(streetClass_list):
    """
    Gets the max of all the default heights of the ASCII arts.

    Parameter: 'streetClass_list', list of all the objects that make up the
        ASCII street art, used to obtain the height of each object.

    Returns: A list of all the default heights of the objects.
    """
    if streetClass_list == []:
        return []

    else:
        return [streetClass_list[0].get_default_height()] + \
            get_all_max_height(streetClass_list[1:])

def get_list_of_classes(street):
    """
    Creates a list of all the objects that are used in the ASCII art. It
    creates each object and puts them into a list.

    Parameter: A list of the key phrases that the user typed in to help create
        each object for the street ASCII art.

    Returns: A list of all the objects that are used for the ASCII art.
    """
    if street == []:

        return []

    else:

        if street[0][0] == "p": 
            p_width, foilage = recur_split(street[0][2:], ",")

            return [Park(int(p_width), foilage)] + \
                get_list_of_classes(street[1:])

        elif street[0][0] == "b":
            b_width, height, brick = recur_split(street[0][2:], ",")

            return [Building(int(b_width), int(height), brick)] \
                + get_list_of_classes(street[1:])
        
        elif street[0][0] == "e":
            e_width, trash_str = recur_split(street[0][2:], ",")

            return [EmptyLot(int(e_width), trash_str)] \
                + get_list_of_classes(street[1:])

def image_list_builder(streetClass_list):
    """
    Has helper functions, 'image_list_builder_helper' and 
    'loop_create_level' fucntions. It creates the image of the ASCII art.

    Parameter: 'streetClass_list', list of all the objects that make up the
        ASCII street art, used to obtain the height of each object.

    Returns: A list of each level of the ASCII art. Each element in the list
        is a sperate level/ line of the ASCII art.
    """
    all_maxs = get_all_max_height(streetClass_list)
    max_height = recur_max(all_maxs)

    return image_list_builder_helper(streetClass_list, 0, max_height)

def loop_create_level(streetClass_list, num, max_height):
    """
    A helper function for 'image_list_builder' that loops over the 
    'streetClass_list' that creates each line of the ASCII art.

    Paramters: 'streetClass_list', list of all the objects that make up the
        ASCII street art, used to obtain the height of each object. 'num', 
        integer that is which level of the ASCII art is needed used with a
        method call. 'max_height' is the max height of the ASCII are and is
        used within a method call.

    Returns: A list of each level of the ASCII art. Each element is a level
        of the ASCII art from each object.
    """
    if streetClass_list == []:
        return ""
    
    else:
        return streetClass_list[0].at_height(num, max_height) + \
            loop_create_level(streetClass_list[1:], num, max_height)
    
def image_list_builder_helper(streetClass_list, num, max_height):
    """
    A helper function for 'image_list_builder'. Calls 'loop_create_level'
    fucntion and adds each line to a list that creates the ASCII image.

    Parameters: 'streetClass_list', list of all the objects that make up the
        ASCII street art, used to obtain the height of each object. 'num', 
        integer that is which level of the ASCII art is needed used with a
        method call. 'max_height' is the max height of the ASCII are and is
        used within a method call.
    """
    if num == max_height:
        return []

    level = loop_create_level(streetClass_list, num, max_height)

    return [level] + \
        image_list_builder_helper(streetClass_list, num + 1, max_height)

def create_street_image(user_input):
    """
    Calls many functions like display, get_list_of_class & image_list_builder
    to create the ASCII image from the user_input.

    Paramter: A string, user input that helps to create each object for the
        image. 'e' is for empty lot, 'p' is for the park. 'b' is for
        the building. The rest of the numbers is used to dictate size and the
        characters are for what texture each object will have.

    Returns: None.
    """
    street = user_input.split()

    streetClass_list = get_list_of_classes(street)

    image_list = image_list_builder(streetClass_list)

    display(image_list)

def main():

    user_input = input("Street: ")
    create_street_image(user_input)

main()
