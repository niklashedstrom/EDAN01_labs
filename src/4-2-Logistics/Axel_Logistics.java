import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Axel_Logistics {
    public static void main(String[] args) {
        int
                graph_size = 6;
        int
                start = 1;
        int
                n_dests = 2;
        int
                [] dest = {5,6};
        int
                n_edges = 9;
        int
                [] from = {1,1,1,2,2,3,3,3,4};
        int
                [] to = {2,3,4,3,5,4,5,6,6};
        int
                [] cost = {6,1,5,5,3,5,6,4,2};

        //int[][] travelMatrix = generateTravelMatrix(to,from,cost,graph_size, n_edges,dest,start);

        findPath(dest, start, graph_size, n_dests, n_edges, cost, to, from);
    }

    static void findPath(int[] dest, int start, int graphSize,  int nDest, int nEdges, int[] cost, int[] to, int[] from){

        Store store = new Store();
        int[][] costMatrix = generateCostMatrix(to,from,cost,graphSize,nEdges,dest,start);
        int maxCost = Arrays.stream(cost).max().orElse(0);
        IntVar[][] nodes = new IntVar[nDest][graphSize];
        IntVar[][] usedRoads = new IntVar[graphSize][graphSize];
        for(int d = 0; d < nDest ; d++){
            for(int n = 0; n < graphSize; n++) {
                nodes[d][n] = new IntVar(store, "node" + (n + 1) + ":" + d, 1, graphSize); // Add itself to domain
            }

            for(int x = 0; x < graphSize; x++){
                for(int y = 0; y < graphSize; y++){
                    if(costMatrix[x][y] == -1){
                        store.impose(new XneqC(nodes[d][x],y+1));
                    }
                }
            }

            store.impose(new XneqC(nodes[d][start-1], start));
            store.impose(new XneqC(nodes[d][dest[d]-1], dest[d]));
            store.impose(new Subcircuit(nodes[d]));

        }
        for(int y = 0; y < graphSize; y++) {
            for (int x = 0; x < graphSize; x++) {
                IntVar usedRoadAll = new IntVar(store, y+"->"+x, 0,1);
                PrimitiveConstraint[] constArr = new PrimitiveConstraint[nDest];
                for(int d = 0 ; d < nDest ; d++){
                    PrimitiveConstraint c = new XeqC(nodes[d][y],x+1);
                    constArr[d] = c;
                }
                store.impose(new Reified(new Or(constArr),usedRoadAll));
                usedRoads[y][x] = usedRoadAll;
            }
        }

        int[] flatCost  = Arrays.stream(costMatrix).flatMapToInt(Arrays::stream).toArray();
        IntVar[] flatUsedRoads = join(usedRoads).toArray(new IntVar[0]);

        IntVar costVar = new IntVar(store, "costVar", 0, Arrays.stream(cost).sum());

        store.impose(new LinearInt(flatUsedRoads,flatCost,"==",costVar));

        Search<IntVar> search = new DepthFirstSearch<>();

        IntVar[] flatNodes = join(nodes).toArray(new IntVar[0]);

        SelectChoicePoint<IntVar> select =
                new SimpleSelect<IntVar>(flatNodes,
                        null,
                        new IndomainMin<IntVar>()
                );

        System.out.println("Number of variables: "+ store.size() +
                "\nNumber of constraints: " + store.numberConstraints());

        boolean result = search.labeling(store,select,costVar);

        if(result){
            System.out.println("\n**** YES");
            System.out.println("Solution : " + java.util.Arrays.asList(flatNodes));
        }else{
            System.out.println("NO SOLUTION");
            //System.out.println(costSum.value());
        }
    }

    static int[][] generateCostMatrix(int[] to, int[] from, int[] cost, int graphSize, int nEdges, int[] dest, int start){

        int[][] costMatrix = new int[graphSize][graphSize];
        for(int[] row : costMatrix){
            Arrays.fill(row, -1);
        }
        for(int n = 0 ; n < nEdges; n++){
            costMatrix[to[n]-1][from[n]-1] = cost[n];
            costMatrix[from[n]-1][to[n]-1] = cost[n];
        }

        for(int d: dest){
            costMatrix[d-1][start-1] = 0;
        }

        for(int n = 0 ; n < graphSize ; n++){
            costMatrix[n][n] = 0;
        }

        return costMatrix;
    }

    static<T> List<T> join(T[][] arrays){
        List<T> list = new ArrayList<>();
        for(T[] row: arrays){
            for(T item: row){
                list.add(item);
            }
        }
        return list;
    }




}