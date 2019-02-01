# Napisz program w dowolnym języku, który:
#
#   Wyznacza relację zależności D (2 p.)
#   Wyznacza postać normalną Foaty FNF([w]) śladu [w] (3 p.)
#   Rysuje graf zależności w postaci minimalnej dla słowa w (3 p.)
#   Wyznacza postać normalną Foaty na podstawie grafu (2 p.)
import graphviz as g


class RelationSet:
    def __init__(self, alphabet, id):
        self.id = id  # string name
        self.alphabet = alphabet  # alphabet is a list of letters
        self.independantRelation = set()  # I
        self.dependantRelation = set()  # D
        self.words = []  # list of words to proceed
        self.stacks = []  # stacs needed to create Foaty normal form
        self.minimalGraphs = []  # contains graphs for words, not really used
        for x in alphabet:  # initializing stacks for each letter in alphabet
            self.stacks.append((x, []))
        # words to najprostszy string

    # end init

    def addToIndependent(self, a, b):
        if a in self.alphabet and b in self.alphabet:  # we can add relation only if both elements are in alphabet
            # we add a,b and b,a relation because I relation is symetric
            self.independantRelation.add((a, b))
            self.independantRelation.add((b, a))

    # end def

    def createDependantRelation(self):
        # we use he fact that D = (sigma)^2 - I
        for x in self.alphabet:
            for y in self.alphabet:
                if not ((x, y) in self.independantRelation):
                    self.dependantRelation.add((x, y))
        # end fors

    # end def

    def addWord(self, word):  # word should be string
        self.words.append(word)

    # end def

    # letter identifies stack
    # symbol is what we append to stack
    def addToLettersStack(self, letter, symbol):
        for (id, stack) in self.stacks:
            if id == letter:
                stack.append(symbol)

    # end def

    # just poping from stack where letter identifies it
    def reduceStack(self, letter):
        for (id, stack) in self.stacks:
            if id == letter:
                stack.pop()

    # end def

    # initializing stacks in order to create Foaty Normal Form
    # the function uses algorithm from page 10 in V. Diekert, Y. Métivier – Partial commutation and traces
    def initializeStacksForWord(self, wordIndex):
        # we need to clear stacks
        self.stacks = []
        for x in self.alphabet:
            self.stacks.append((x, []))

        word = self.words[wordIndex]
        # we need to take it reverse
        wordReverse = word[::-1]
        # we will use D relation to make stacks
        self.createDependantRelation()
        # we proceed each letter in wordReverse operating on the stacks
        for wordLetter in wordReverse:
            # we always need to add this letter to it's own stack
            self.addToLettersStack(wordLetter, wordLetter)
            for alphabetLetter in self.alphabet:
                # if our letters are in dependent relation and are different we add to alphabetLetter's stack '*'
                if alphabetLetter != wordLetter and (wordLetter, alphabetLetter) in self.dependantRelation:
                    self.addToLettersStack(alphabetLetter, '*')
        # uncomment this to see stacks after this function
        ''' 
        #printing the result
        for (id,stack) in self.stacks:
            print(" _")
            print("|" + id + "|" )
            print("|_|")
            for elem in stack:
                print("|" + elem + "|")
            print(" _")
        #end print
        '''

    # end def

    def ifEmptyStacks(self):
        for (id, stack) in self.stacks:
            if len(stack) > 0:
                return False
        return True

    # end def

    # the function uses algorithm from page 10 in V. Diekert, Y. Métivier – Partial commutation and traces
    # we use created befor stack to create Foaty Normal Form
    def reduceStacks(self):
        # we need to find first stack with letter
        foatyNormalForm = "("
        toBeReduce = []

        # we need to proceed until each stack is empty
        while not self.ifEmptyStacks():
            for (letter, stack) in self.stacks:
                # if we find stack that's top is not * we add it to foaty form
                if len(stack) > 0 and stack[-1] != '*':
                    toBeReduce.append(stack[-1])
                    stackLetter = stack[-1]
                    # we add
                    foatyNormalForm += stackLetter
            # then we need to close the part and start new one
            foatyNormalForm += ")("
            # for each used top of stacs we need to reduce
            for toReduce in toBeReduce:
                for alphabetLetter in self.alphabet:
                    # we reduce stacks only if two letter from alphabet were in D relation
                    if (toReduce, alphabetLetter) in self.dependantRelation:
                        # then we need to reduce stack with id == alphabetLetter
                        self.reduceStack(alphabetLetter)
            # we need to clear ToBeReduce before next while step
            toBeReduce = []
        # we need to remove last char which is '('
        foatyNormalForm = foatyNormalForm[:-1]
        # print(foatyNormalForm)
        return foatyNormalForm

    # end def

    # running whole process involve using this 2 methods in this order wordIndex tells us which word to proceed
    def foatyNormalForm(self, wordIndex):
        self.initializeStacksForWord(wordIndex)
        return self.reduceStacks()

    # end def

    # the function creates minimal digraph for wordIndex
    def createGraph(self, wordIndex):
        # naming the graph
        H = g.Digraph(comment='Graph ' + self.words[wordIndex], filename=self.id + "_" + self.words[wordIndex] + ".gv")
        MIN = []
        Edges = []
        Nodes = []
        word = self.words[wordIndex]

        # proceeding ale letters in word from right to left
        for i in range(len(word) - 1, -1, -1):
            Nodes.append((str(i), word[i]))  # we add each node to list of nodes
            MIN.append((str(i), word[i]))

            for x in range(len(MIN)):  # iterating through all nodes in MIN
                (idAsString, letter) = MIN[x]
                if (int(idAsString), letter) == (i, word[i]):  # we avoid the connecting same nodes
                    continue
                # we search if current node is in relation D with nodes already proceeded
                if (word[i], letter) in self.dependantRelation:
                    # if yes we add such edge
                    Edges.append((str(i), idAsString))
        # we have created graph although it is to big and we need to remove unnecessary edges

        # we iterate thorugh all edges
        for eIndex in range(len(Edges)):
            # check whether the edge isn't already removed
            if Edges[eIndex] == '\0':
                continue
            # unpacking the edge
            (source, destiny) = Edges[eIndex]
            # counting how many paths are from source to destiny
            # if more than one then edge is unnecessary and can be removed
            if countAnotherWays(Edges, source, destiny) > 1:
                Edges[eIndex] = '\0'

        # after removing unnecessary edges we filter them deleting null chars
        Edges = [x for x in Edges if x != '\0']

        # we assign optimized graph to graphviz structure to visualize it
        for n in Nodes:
            (i, j) = n
            H.node(i, j)
        for e in Edges:
            (i, j) = e
            H.edge(i, j)
        self.minimalGraphs.append(H)
        return H, Nodes, Edges  # returning edges and nodes in other form to ease future calculations


# end class

# function needed to calculate number of connections between two points
def countAnotherWays(Edges, s, d):
    ways = 0
    for e in Edges:
        if e == '\0':
            continue
        s2, d2 = e
        # if we find source edge
        if s2 == s:
            # if this is destiny we searched for increment
            if d2 == d:
                ways += 1
            # if it was another edge for s we try to find the way from it to the destiny
            else:
                ways += countAnotherWays(Edges, d2, d)
    return ways  # returning number of ways counted


# end def

# function that creates Foaty normal form from minimal graph
def fromGraphToFoatyForm(nodes, edges):
    # collection of nodes with it's ranks
    #  rank means how many edges we need to pass to achieve this node from 0 rank node
    nodeWithRanks = []
    # we search for 0 rank points (point that it is not destiny in any edge
    for (node, label) in nodes:
        isFirstNode = True  # we assume that we find first point
        for (_, destiny) in edges:
            # if we find any edge that's destiny is our point we reject it
            if destiny == node:
                isFirstNode = False
                break
        # end for
        # if we found correct node we add it to list wit 0 rank
        if isFirstNode:
            nodeWithRanks.append((node, 0))

    rankToProceed = 0  # rank points that we currently proceed
    hasChanged = True
    while (hasChanged):
        hasChanged = False  # we assume that iteration hasn't changed anything not to have infinite loop
        toBeAdded = []  # list of points required to me added to rank list
        for node, rank in nodeWithRanks:
            # if we find node with rank to proceed
            if rank == rankToProceed:
                # we check all edges from node and assign rank+1 to its destiny
                for node1, node2 in edges:
                    if node1 == node:
                        hasChanged = True  # we note the change
                        toBeAdded.append((node2, rank + 1))  # we will add that points later to the list with ranks
                    # end if
                # end for
            # end if
        # end for
        # we need to add new nodes
        for node, rank in toBeAdded:  # we need to add all new points ALSO updated ones
            # we remove previous records of points (we need to store record with highest rank)
            # newly added points always have higher rank
            nodeWithRanks = [(n, r) for (n, r) in nodeWithRanks if n != node]
            nodeWithRanks.append((node, rank))
        # we need to proceed next rank
        rankToProceed += 1

    # in order to see resoult of previous fragment please uncomment
    """
    print(nodeWithRanks)
    print(nodes)
    print(edges)
    print(rankToProceed)
    """
    foatyNormalForm = ""
    # from 0 rank to highest rank we add the letters in each parenthesis
    # (rank0 letter)(rank1 letter)...(rankI letters)
    for currentRank in range(rankToProceed):
        foatyNormalForm += "("  # for each rank we need to open fragment
        for node, rank in nodeWithRanks:  # we search for nodes with current rank to proceed
            if rank == currentRank:
                # we need to find its label (in fact it is mapping to alphabet)
                label = '\0'
                for n, l in nodes:
                    if n == node:
                        label = l
                # we adds it alphabet id to foaty form
                foatyNormalForm += label
        foatyNormalForm += ")"  # for each we need to close
    # we have Foaty normal form
    return foatyNormalForm


# end def

# firs example
def runFirst():
    print("FIRST:\n")
    P = RelationSet(['a', 'b', 'c', 'd'], "First")
    P.addToIndependent('a', 'd')
    P.addToIndependent('b', 'c')
    P.createDependantRelation()
    word = "baadcb"
    print("I:\n" + repr(P.independantRelation))
    print("D:\n" + repr(P.dependantRelation))
    P.addWord(word)
    print("Word: \n" + word)
    print("Foaty normal form:\n" + P.foatyNormalForm(0))
    H, nodes, edges = P.createGraph(0)
    print("Graph received: ")
    print(H)
    H.view()
    print("From graph to Foaty normal form:")
    print(fromGraphToFoatyForm(nodes, edges))

    print("\n\n")


# end def

# second examle
def runSecond():
    print("SECOND:\n")
    P = RelationSet(['a', 'b', 'c', 'd', 'e', 'f'], "Second")
    P.addToIndependent('a', 'd')
    P.addToIndependent('b', 'e')
    P.addToIndependent('c', 'd')
    P.addToIndependent('c', 'f')
    P.createDependantRelation()
    word = "acdcfbbe"
    print("I:\n" + repr(P.independantRelation))
    print("D:\n" + repr(P.dependantRelation))
    P.addWord(word)
    print("Word: \n" + word)
    print("Foaty normal form:\n" + P.foatyNormalForm(0))
    H, nodes, edges = P.createGraph(0)
    print("Graph received: ")
    print(H)
    H.view()
    print("From graph to Foaty normal form:")
    print(fromGraphToFoatyForm(nodes, edges))

    print("\n\n")


# end def

# Third example
def runThird():
    print("THIRD: \n")
    P = RelationSet(['a', 'b', 'c', 'd', 'e'], "Third")
    P.addToIndependent('b', 'e')
    P.addToIndependent('c', 'a')
    P.addToIndependent('a', 'd')
    P.addToIndependent('b', 'd')
    P.createDependantRelation()
    word = "acebdac"
    print("I:\n" + repr(P.independantRelation))
    print("D:\n" + repr(P.dependantRelation))
    P.addWord(word)
    print("Word: \n" + word)
    print("Foaty normal form:\n" + P.foatyNormalForm(0))
    H, nodes, edges = P.createGraph(0)
    print("Graph received: ")
    print(H)
    H.view()
    print("From graph to Foaty normal form:")
    print(fromGraphToFoatyForm(nodes, edges))

    print("\n\n")


# end def

if __name__ == "__main__":
    runFirst()
    runSecond()
    runThird()
