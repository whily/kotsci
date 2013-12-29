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

Quick reference
---------------

### Creation

| Operation | Scasci | Matlab | Numpy |
| --------- | ------ | ------ | ----- |
| Zeroed matrix | TODO  | `c = zeros(n,m)` | `c = zeros((n,m))` |
| Zeroed vector | TODO | `a = zeros(n)` | `a = zeros(n)` |
| Vector of ones | TODO | `a = ones(n)` | `a = ones(n)` |
| Vector of particular number | TODO | `a = ones(n) * 5` | `a = ones(n) * 5` |
| Identity matrix | TODO | `eye(n)` | `eye(n)` |
| Diagonal matrix |TODO | `diag([1 2 3])` | `diag((1,2,3))` |
| Matrix inline creation | TODO  | `a = [1 2; 3 4]` | `a = array([ [1,2], [3,4] ])` |
| Column vector inline creation | TODO | `a = [1 2 3 4]` | `a=array([1,2,3,4])` |
| Row  vector inline creation | TODO | `a = [1,2,3,4]'` | `a = array([1,2,3]).reshape(-1,1)` |

### Manipulation

### Operations

### Functions
