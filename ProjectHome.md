# ECS289 – Data Mining Assignment 1 - NetFlix #

**Team Members: Justin Becker, Philip Fisher-Ogden**
_Team Name_: TeamSpelunker
_Project Name_: MovieMiner

## Outline ##
In this proposal we introduce the recommendation system MovieMiner. The system's primary objective is to predict the future movie ratings of customers. The training data that will be used is a subset of the Netflix competition data, which consists of millions of customer movie ratings (<movie id, customer id, rating, date recommended>). The test data provides users and movies (<customer id, movie id, ?, date>) and asks the system to predict a rating.

MovieMiner's design choices take into consideration the assignment guidelines. The first guideline is that no external data sources are permitted. To meet this constraint and provide a useful system MovieMiner will leverage collaborative filtering techniques. The underlying assumption of collaborative filtering is: "those who agreed in the past tend to agree again in the future" [1](1.md). In line with the first guideline, collaborative filtering does not take into consideration external domain knowledge. All that is required of a simple collaborative filtering system is previously acquired data.  The second assignment guideline is that the system can leverage association rule mining, clustering, and classification techniques.  We intend on building a k-Nearest Neighbor classifier [2, 4, 5] to provide our item-based movie ratings predictions.  We have decided to use only item-based collaborative filtering [11, 12] for the k-Nearest Neighbor algorithm, rather than a combination of user-based and item-based approaches (see [13](13.md) for a discussion on how both could be used).  The main motivating factor for this decision is that the number of items is significantly lower than the number of users, making it more efficient to pre-compute the item-item similarities matrix than the user-user one.

Our approach is to use collaborative filtering with item-based similarities to predict the unlabeled ratings in the test data.  We will build an item-item similarities matrix from the training data, where an entry rij in the matrix will contain a measure of similarity between movies i and j.  The similarity scores will be calculated using the Pearson correlation coefficient between movies, taken across their common raters.  To identify a rating r for item i and user u, the top k similar items (neighbors) that were rated by u will be retrieved from the item-item similarities matrix.  The predicted rating r for item i and user u will be the sum of interpolation weights and u's rating values for the top-k similar items.  The interpolation weights will be computed as a global solution to an optimization problem (described in detail in \[4\]).

### References ###
|[1.] http://en.wikipedia.org/wiki/Collaborative_filtering|
|:--------------------------------------------------------|
|[2.] http://en.wikipedia.org/wiki/Nearest_neighbor_(pattern_recognition)|
|[3.] http://en.wikipedia.org/wiki/Slope_One|
|[4.] Improved Neighborhood-based Collaborative Filtering - http://public.research.att.com/%7Eyehuda/pubs/cf-workshop.pdf|
|[5.] Use of KNN for the Netflix Prize - http://www.stanford.edu/class/cs229/proj2006/HongTsamis-KNNForNetflix.pdf|
|[6.] Slope One Predictors for Online Rating-Based Collaborative Filtering - http://arxiv.org/abs/cs/0702144|
|[7.] TiVo: making show recommendations using a distributed collaborative filtering architecture - http://portal.acm.org/citation.cfm?doid=1014097|
|[8.] Cofi: A Java-Based Collaborative Filtering Library - http://www.nongnu.org/cofi/|
|[9.] Taste: Collaborative Filtering for Java - http://taste.sourceforge.net/|
|[10.] An algorithmic framework for performing collaborative filtering -http://portal.acm.org/citation.cfm?id=312624.312682 |
|[11.] Amazon.com Recommendations - Item-to-Item Collaborative Filtering - http://www.cs.helsinki.fi/u/gionis/linden03amazon.pdf|
|[12.] Item-based Collaborative Filtering Recommendation Algorithms - http://www-users.cs.umn.edu/~sarwar/sdm.pdf|
|[13.] Unifying user-based and item-based collaborative filtering approaches by similarity fusion - http://portal.acm.org/citation.cfm?id=1148257|
|[14.] http://public.research.att.com/~yehuda/pubs/BellKorIcdm07.pdf|
|[15.] http://www.netflixprize.com/community/viewtopic.php?pid=6032|