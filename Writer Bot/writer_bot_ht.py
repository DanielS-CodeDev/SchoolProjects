"""
File: writer_bot_ht.py
Author: Daniel S
Course: CSC120, Spring 2025
Purpose: This program prompts a user to input a file name, a prefix length and
    how many words does the user want the program to generate. This program
    follows the Markov Chain Algorithm. The program creates a table of prefixs
    which are worlds from the file of length provided by the user and has a
    single word suffix. The algorithm cycles through the prefixs and suffixs
    and creates a random generate text of words that are put together. The
    table helps the algorithm put the words together in a meaningful order
    like the input text did.
"""
import sys
import random
SEED = 8

class Hashtable:
    """
    This class represents a Hash Table of a specified size.

    This class defines numerous getter, setter and helper functions for the
    class. The getter method gets the value of the key specified from the
    table. The setter puts the key value pairs in the hash table. The helpers
    help get the hash value and another helps loop over the table with linear
    probing. There is two special methods, 'str' and 'contains'.
    """
    def __init__(self, size):
        """
        This is the constructor of the class that sets up the attributes for
        the class. Sets up the hash table and defines the size attribute.

        Parameters: 'self', instance of the class. 'size', integer that
            determines the size of the hash table.

        Returns: None.
        """
        self._size = size
        self._pairs = [None] * self._size

    def _hash(self, key):
        """
        Gets hash index for hash table. Creates a polynomial using Horner's
        rule, which helps find the hash index.

        Parameters: 'self', instance of the class. 'key', string which is the
            key for a hash value, which is looped over to find hash index.

        Returns: An integer, the hash index of a key for the hash table.
        """
        p = 0
        for c in key:
            p = 31*p + ord(c)
        return p % self._size

    def put(self, key, value):
        """
        Puts the key/ value pair into the hash table. If the key already
        exists then the value is appended to the the exisiting values list.

        Parameters: 'self', instance of the class. 'key', string used in
            arguments and is put into the hash table. 'value', string used
            to be put into the hash table.

        Returns: None.
        """
        hash_value = self._hash(key)

        if key in self:
            hash_value = self.find_key_hash_value(hash_value, key)
            self._pairs[hash_value][1].extend(value)

        else:
            # linear probing
            if self._pairs[hash_value] != None:

                while self._pairs[hash_value] != None:

                    if hash_value <= 0:
                        hash_value = len(self._pairs) - 1
                    else:
                        hash_value -= 1

            self._pairs[hash_value] = [key, value]

    def get(self, key):
        """
        Gets a value from the hash table from a provided key.

        Parameters: 'self', instance of the class. 'key', string is used in
            arguments and is used to retrieve a value from the hash table.

        Returns: If the key is in the table it retuens the value that
            corresponds to the key. If the key is not in the table 'None' is
            returned.
        """
        hash_value = self._hash(key)

        if key in self:
            hash_value = self.find_key_hash_value(hash_value, key)
            return self._pairs[hash_value][1]

        return None

    def find_key_hash_value(self, hash_value, key):
        """
        Helper method used in 'get' and 'put' to loop over the list and
        changes the 'hash_value' using linear probing to find a key.

        Parameters: 'self', instance of the class. 'hash_value', integer which
            is the hash index for the hash table and is decremented to find
            the key in the table. 'key', string used to compare to values
            within the hash table.

        Returns: An integer, the modified hash value/ index for the hash
            table.
        """
        if self._pairs[hash_value][0] != key:

            while self._pairs[hash_value][0] != key:

                if hash_value <= 0:
                    hash_value = len(self._pairs) - 1
                else:
                    hash_value -= 1

        return hash_value

    def __contains__(self, key):
        """
        Loops over the hash table and returns true if the key is in the table
        or false if the key is not in the table.

        Parameters: 'self', instance of the class. 'key', string used to
            compare to values within the hash table.
        """
        hash_value = self._hash(key)

        # If it hits None then it is not in the list.
        while self._pairs[hash_value] != None:

            if self._pairs[hash_value][0] == key:
                return True

            if hash_value <= 0:
                hash_value = len(self._pairs) - 1
            else:
                hash_value -= 1

        return False

    def __str__(self):
        return str(self._pairs)

def create_table(filename, prefix_size, hashtable_size):
    """
    Creates a list of words in the order that are in the file and creates a
    prefix suffix dictionary from the file.

    Parameters: 'filename', string that is the file that is opened and used.
        'prefix_size' is an integer that is used to determine the size of the
        prefix for the table.

    Returns: A dictionary and a list. The dictionaries keys are tuples that
        are the prefixes. The values are a list of the suffixs that follow
        the prefix. The list is the list of every word from the file in order.
    """
    NONWORD = "@"
    word_hash_table = Hashtable(hashtable_size)

    # put in the length of prefix for the NONWORDS
    all_words_list = [NONWORD] * prefix_size

    file = open(filename, "r")

    for line in file:
        all_words_list.extend(line.strip().split())

    for i in range(len(all_words_list)):

        # create the tuples and the suffix
        prefix_key = " ".join(all_words_list[i:prefix_size + i])

        suffix_endpoints = prefix_size + i
        suffix_value = all_words_list[suffix_endpoints: suffix_endpoints + 1]

        if suffix_value == []:
            break

        # Both key and value are of type list
        word_hash_table.put(prefix_key, suffix_value)

    file.close()
    return word_hash_table, all_words_list

def create_text(word_hash_table, num_read_words, prefix_size, all_words_list):
    """
    Creates the generate text from the prefix - suffix table. It generates
    a number of words that the user inputed.

    Parameters: 'word_hash_table', class object, hash table, used to obtain
        prefix and suffix which are words that follow the prefix. It is used
        to create the generated text. 'num_read_words' is an integer the cap
        on how many words that is generated. 'prefix_size' an integer used to
        set up the prefix size for slicing. 'all_words_list', list of every
        word from the file in order, used to help get the first few words from
        the text.

    Returns: A list, the generated text of create from the prefix (the key) -
        suffix (value) hash table.
    """
    # get the first couple of words 
    prefix = all_words_list[prefix_size:prefix_size * 2]
    tlist = []
    tlist.extend(prefix)
    index = 0

    while len(tlist) < num_read_words:

        table_suffix = word_hash_table.get(" ".join(prefix))

        if len(table_suffix) > 1:
            ran_index = random.randint(0, len(table_suffix) - 1)
            new_word = table_suffix[ran_index]

        else:
            new_word = table_suffix[0]

        tlist.append(new_word)

        index += 1
        prefix = tlist[index:]

    return tlist

def print_generate_text(tlist):
    """
    Prints out the generated text list only 10 words max per line.

    Parameters: 'tlist', the generated text of create from the prefix
        (the key) - suffix (value) hash table.

    Returns: None.
    """
    num_words = 1
    for word in tlist:

        if num_words == 10:
            print(word, end="\n")
            num_words = 0

        else:
            print(word, end=" ")

        num_words += 1

def main():

    random.seed(SEED)

    filename = input()

    hashtable_size = int(input())

    prefix_size = int(input())

    if prefix_size < 1:
        print("ERROR: specified prefix size is less than one")
        sys.exit(0)

    num_read_words = int(input())

    if num_read_words < 1:
        print("ERROR: specified size of the generated text is less than one")
        sys.exit(0)

    hash_table, all_words_list = create_table(filename, prefix_size, 
                                                            hashtable_size)

    tlist = create_text(hash_table, num_read_words, prefix_size, 
                                                            all_words_list)
    print_generate_text(tlist)

main()
