Linear Algebra
==============

Like [breeze](https://github.com/scalanlp/breeze), the target of linear algebra
library in scasci is to provide Matlab/NumPy like interfaces.

Core concept
------------

Like Matlab, both vector and matrix are treated in the same manner,
i.e. represented in class Matrix. Vector is treated as a special
matrix, i.e. nx1 matrix. Matrix storage is column major, while index
starting from 0.
